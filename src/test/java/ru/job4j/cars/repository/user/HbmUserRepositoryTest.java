package ru.job4j.cars.repository.user;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HbmUserRepositoryTest {

    private HbmUserRepository repository;
    private SessionFactory sf;

    @BeforeEach
    void init() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();

        repository = new HbmUserRepository(new CrudRepository(sf));
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
    }

    @Test
    void whenAddNewUserThenFindByIdReturnsSameUser() {
        User user = new User();
        user.setLogin("login");
        user.setPassword("password");
        repository.create(user);

        User result = repository.findById(user.getId()).orElseThrow();

        assertThat(result.getLogin()).isEqualTo("login");
        assertThat(result.getPassword()).isEqualTo("password");
    }

    @Test
    void whenUpdateUserThenFindByIdReturnsUpdated() {
        User user = new User();
        user.setLogin("old login");
        repository.create(user);

        user.setLogin("new login");
        repository.update(user);

        User result = repository.findById(user.getId()).orElseThrow();
        assertThat(result.getLogin()).isEqualTo("new login");
    }

    @Test
    void whenDeleteUserThenFindByIdReturnsEmpty() {
        User user = new User();
        user.setLogin("login");
        repository.create(user);
        int id = user.getId();

        repository.delete(id);
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void whenFindAllOrderByIdThenReturnsAllInOrder() {
        User first = new User();
        first.setLogin("first");
        repository.create(first);

        User second = new User();
        second.setLogin("second");
        repository.create(second);

        List<User> result = repository.findAllOrderById();
        assertThat(result)
                .hasSize(2)
                .extracting(User::getLogin)
                .containsExactly("first", "second");
    }

    @Test
    void whenFindByLoginThenReturnsCorrectUser() {
        User user1 = new User();
        user1.setLogin("login1");
        user1.setPassword("password1");
        repository.create(user1);

        User user2 = new User();
        user2.setLogin("login2");
        user2.setPassword("password2");
        repository.create(user2);

        Optional<User> result = repository.findByLogin("login1");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(User::getPassword)
                .isEqualTo("password1");
        assertThat(repository.findByLogin("login3")).isEmpty();
    }

    @Test
    void whenFindByLikeLoginThenReturnsMatchingUsers() {
        User user1 = new User();
        user1.setLogin("admin");
        repository.create(user1);

        User user2 = new User();
        user2.setLogin("moderator");
        repository.create(user2);

        User user3 = new User();
        user3.setLogin("user");
        repository.create(user3);

        List<User> result = repository.findByLikeLogin("mod");
        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(User::getLogin)
                .isEqualTo("moderator");
    }
}