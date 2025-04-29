package ru.job4j.cars.repository.owner;

import org.hibernate.SessionFactory;
import org.hibernate.TransientObjectException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.HistoryOwner;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.CrudRepository;
import ru.job4j.cars.repository.car.HbmCarRepository;
import ru.job4j.cars.repository.historyowner.HbmHistoryOwnerRepository;
import ru.job4j.cars.repository.user.HbmUserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HbmOwnerRepositoryTest {

    private HbmOwnerRepository repository;
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
        repository = new HbmOwnerRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
    }

    @Test
    void whenCreateOwnerThenFindByIdReturnsSame() {
        Owner owner = new Owner();
        owner.setName("Owner");
        repository.create(owner);

        Owner result = repository.findById(owner.getId()).orElseThrow();

        assertThat(result.getName()).isEqualTo("Owner");
        assertThat(result.getUser()).isNull();
        assertThat(result.getHistoryOwners()).isEmpty();
    }

    @Test
    void whenCreateOwnerWithAllDependenciesThenFindByIdReturnsSame() {
        User user = createTestUser();

        Owner owner = new Owner();
        owner.setName("Owner");
        owner.setUser(user);
        repository.create(owner);

        Car car = new Car();
        car.setName("Car");
        new HbmCarRepository(crudRepository).create(car);

        HistoryOwner historyOwner = new HistoryOwner();
        historyOwner.setCar(car);
        historyOwner.setOwner(owner);
        new HbmHistoryOwnerRepository(crudRepository).create(historyOwner);

        Owner result = repository.findById(owner.getId()).orElseThrow();

        assertThat(result.getName()).isEqualTo("Owner");
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getHistoryOwners())
                .hasSize(1)
                .first()
                .extracting(
                        HistoryOwner::getId,
                        hOwner -> hOwner.getCar().getName())
                .containsExactly(historyOwner.getId(), "Car");
    }

    @Test
    void whenCreateOwnerWithoutUserThenThrowException() {
        Owner owner = new Owner();
        owner.setName("Owner");
        owner.setUser(new User());

        assertThatThrownBy(() -> repository.create(owner))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(TransientObjectException.class)
                .hasMessageContaining("save the transient instance before flushing");
    }

    @Test
    void whenUpdateOwnerThenChangesPersisted() {
        Owner owner = createTestOwner();
        String newName = "Updated Name";
        owner.setName(newName);
        repository.update(owner);

        Owner updated = repository.findById(owner.getId()).orElseThrow();

        assertThat(updated.getName()).isEqualTo(newName);
    }

    @Test
    void whenDeleteOwnerThenNotFound() {
        Owner owner = createTestOwner();
        int id = owner.getId();

        repository.delete(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void whenFindAllOrderByIdThenReturnsCorrectList() {
        Owner first = createTestOwner();
        Owner second = createTestOwner();

        List<Owner> result = repository.findAllOrderById();

        assertThat(result)
                .hasSize(2)
                .extracting(Owner::getId)
                .containsExactly(first.getId(), second.getId());
        assertThat(result.getFirst().getUser().getPassword()).isEqualTo("password");
        assertThat(result.getFirst().getHistoryOwners()).isEmpty();
    }

    @Test
    void whenFindByIdNotExistThenReturnEmpty() {
        assertThat(repository.findById(1000)).isEmpty();
    }

    private Owner createTestOwner() {
        User user = createTestUser();
        Owner owner = new Owner();
        owner.setName("Test Owner");
        owner.setUser(user);
        repository.create(owner);
        return owner;
    }

    private User createTestUser() {
        User user = new User();
        user.setLogin("login");
        user.setPassword("password");
        new HbmUserRepository(crudRepository).create(user);
        return user;
    }
}