package ru.job4j.cars.repository.post;

import org.hibernate.SessionFactory;
import org.hibernate.TransientObjectException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CrudRepository;
import ru.job4j.cars.repository.car.HbmCarRepository;
import ru.job4j.cars.repository.file.HbmFileRepository;
import ru.job4j.cars.repository.pricehistory.HbmPriceHistoryRepository;
import ru.job4j.cars.repository.user.HbmUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HbmPostRepositoryTest {

    private HbmPostRepository repository;
    private SessionFactory sf;
    private CrudRepository crudRepository;

    @BeforeEach
    void init() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();

        sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();

        crudRepository = new CrudRepository(sf);
        repository = new HbmPostRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
    }

    @Test
    void whenAddNewPostThenFindByIdReturnsSamePost() {
        Post post = new Post();
        post.setDescription("test");
        repository.create(post);

        Post result = repository.findById(post.getId()).orElseThrow();

        assertThat(result.getDescription()).isEqualTo("test");
        assertThat(result.getUser()).isNull();
        assertThat(result.getCar()).isNull();
        assertThat(result.getPriceHistories()).isEmpty();
        assertThat(result.getParticipates()).isEmpty();
        assertThat(result.getPhotos()).isEmpty();
    }

    @Test
    void whenAddNewPostWithAllDependenciesThenFindByIdReturnsSamePost() {
        User user = new User();
        user.setLogin("user1");
        new HbmUserRepository(crudRepository).create(user);
        Car car = new Car();
        car.setName("KIA");
        new HbmCarRepository(crudRepository).create(car);
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setBefore(1000);
        priceHistory.setAfter(2000);
        new HbmPriceHistoryRepository(crudRepository).create(priceHistory);
        File photo = new File();
        photo.setName("photo1.jpg");
        new HbmFileRepository(crudRepository).save(photo);
        Post post = new Post();
        post.setDescription("Car for sale");
        post.setUser(user);
        post.setCar(car);
        post.getPriceHistories().add(priceHistory);
        post.getParticipates().add(user);
        post.getPhotos().add(photo);
        repository.create(post);
        Post result = repository.findById(post.getId()).orElseThrow();

        assertThat(result.getDescription()).isEqualTo("Car for sale");
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getCar()).isEqualTo(car);
        assertThat(result.getPriceHistories())
                .first()
                .extracting(PriceHistory::getId)
                .isEqualTo(priceHistory.getId());
        assertThat(result.getParticipates())
                .first()
                .extracting(User::getId)
                .isEqualTo(user.getId());
        assertThat(result.getPhotos())
                .first()
                .extracting(File::getId)
                .isEqualTo(photo.getId());
    }

    @Test
    public void whenAddPostWithUnsavedUserThenPostNotSaved() {
        Post post = new Post();
        post.setDescription("test");
        User user = new User();
        user.setLogin("unsaved");
        post.setUser(user);

        assertThatThrownBy(() -> repository.create(post))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(TransientObjectException.class)
                .hasMessageContaining("save the transient instance before flushing");
    }

    @Test
    public void whenTestFindAllOrderById() {
        Post first = new Post();
        first.setDescription("First");
        Post second = new Post();
        second.setDescription("Second");
        repository.create(first);
        repository.create(second);

        Post result = repository.findAllOrderById().getFirst();

        assertThat(result).isEqualTo(first);
        assertThat(result.getUser()).isNull();
        assertThat(result.getCar()).isNull();
        assertThat(result.getPriceHistories()).isEmpty();
        assertThat(result.getParticipates()).isEmpty();
        assertThat(result.getPhotos()).isEmpty();
    }

    @Test
    public void whenUpdatePostIsSuccessful() {
        Post post = new Post();
        post.setDescription("Old description");
        repository.create(post);
        int id = post.getId();
        Post updatePost = new Post();
        updatePost.setId(id);
        updatePost.setDescription("New description");
        repository.update(updatePost);

        assertThat(repository.findById(id).orElseThrow().getDescription())
                .isEqualTo("New description");
    }

    @Test
    public void whenUpdatePostIsNotSuccessful() {
        Post post = new Post();
        post.setDescription("Old description");
        repository.create(post);
        Post updatePost = new Post();
        updatePost.setDescription("New description");
        repository.update(updatePost);

        assertThat(repository.findById(post.getId()).orElseThrow().getDescription())
                .isEqualTo("Old description");
    }

    @Test
    public void whenDeletePostIsSuccessful() {
        Post post = new Post();
        post.setDescription("To be deleted");
        repository.create(post);
        int id = post.getId();
        repository.delete(id);

        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    public void whenDeletePostIsNotSuccessful() {
        Post post = new Post();
        post.setDescription("Will not be deleted");
        repository.create(post);
        repository.delete(1000);

        assertThat(repository.findById(post.getId()).orElseThrow()).isEqualTo(post);
    }

    @Test
    public void whenFindByCreatedAfter() {
        Post oldPost = new Post();
        oldPost.setDescription("Old post");
        oldPost.setCreated(LocalDateTime.now().minusDays(2));
        repository.create(oldPost);

        Post newPost = new Post();
        newPost.setDescription("New post");
        repository.create(newPost);

        List<Post> results = repository.findByCreatedAfter();

        assertThat(results)
                .hasSize(1)
                .first()
                .extracting(Post::getDescription)
                .isEqualTo("New post");
    }

    @Test
    public void whenFindAllWithPhotos() {
        Post postWithoutPhoto = new Post();
        postWithoutPhoto.setDescription("No photo");
        repository.create(postWithoutPhoto);

        File photo = new File();
        photo.setName("photo.jpg");
        new HbmFileRepository(crudRepository).save(photo);

        Post postWithPhoto = new Post();
        postWithPhoto.setDescription("With photo");
        postWithPhoto.getPhotos().add(photo);
        repository.create(postWithPhoto);

        List<Post> results = repository.findAllWithPhotos();

        assertThat(results)
                .hasSize(1)
                .first()
                .extracting(Post::getDescription)
                .isEqualTo("With photo");
    }

    @Test
    public void whenFindByLikeCarBrand() {
        Car kia = new Car();
        kia.setName("KIA Ceed");
        new HbmCarRepository(crudRepository).create(kia);

        Car uaz = new Car();
        uaz.setName("UAZ Hunter");
        new HbmCarRepository(crudRepository).create(uaz);

        Post post1 = new Post();
        post1.setDescription("KIA post");
        post1.setCar(kia);
        repository.create(post1);

        Post post2 = new Post();
        post2.setDescription("UAZ post");
        post2.setCar(uaz);
        repository.create(post2);

        List<Post> results = repository.findByLikeCarBrand("KIA");

        assertThat(results)
                .hasSize(1)
                .first()
                .extracting(p -> p.getCar().getName())
                .isEqualTo("KIA Ceed");
    }
}