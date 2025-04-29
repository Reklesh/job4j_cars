package ru.job4j.cars.repository.historyowner;

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
import ru.job4j.cars.repository.CrudRepository;
import ru.job4j.cars.repository.car.HbmCarRepository;
import ru.job4j.cars.repository.owner.HbmOwnerRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HbmHistoryOwnerRepositoryTest {

    private HbmHistoryOwnerRepository repository;
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
        repository = new HbmHistoryOwnerRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
    }

    @Test
    void whenCreateHistoryOwnerThenFindByIdReturnsSame() {
        Car car = createTestCar();
        Owner owner = createTestOwner();
        HistoryOwner historyOwner = new HistoryOwner();
        historyOwner.setCar(car);
        historyOwner.setOwner(owner);
        repository.create(historyOwner);

        HistoryOwner result = repository.findById(historyOwner.getId()).orElseThrow();

        assertThat(result.getCar()).isEqualTo(car);
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getStartAt()).isNotNull();
    }

    @Test
    void whenAddHistoryOwnerWithUnsavedCarThenThrowException() {
        Owner owner = createTestOwner();
        HistoryOwner historyOwner = new HistoryOwner();
        historyOwner.setCar(new Car());
        historyOwner.setOwner(owner);

        assertThatThrownBy(() -> repository.create(historyOwner))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(TransientObjectException.class)
                .hasMessageContaining("save the transient instance before flushing");
    }

    @Test
    void whenUpdateHistoryOwnerThenChangesPersisted() {
        HistoryOwner historyOwner = createTestHistoryOwner();
        repository.create(historyOwner);
        LocalDateTime newEndAt = LocalDateTime.now().plusDays(1).withNano(0);
        historyOwner.setEndAt(newEndAt);
        repository.update(historyOwner);

        HistoryOwner updated = repository.findById(historyOwner.getId()).orElseThrow();

        assertThat(updated.getEndAt()).isEqualTo(newEndAt);
    }

    @Test
    void whenDeleteHistoryOwnerThenNotFound() {
        HistoryOwner historyOwner = createTestHistoryOwner();
        repository.create(historyOwner);
        int id = historyOwner.getId();

        repository.delete(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void whenFindAllOrderByIdThenReturnsCorrectList() {
        HistoryOwner first = createTestHistoryOwner();
        HistoryOwner second = createTestHistoryOwner();
        repository.create(first);
        repository.create(second);

        var result = repository.findAllOrderById();

        assertThat(result)
                .hasSize(2)
                .extracting(HistoryOwner::getId)
                .containsExactly(first.getId(), second.getId());
    }

    private HistoryOwner createTestHistoryOwner() {
        Car car = createTestCar();
        Owner owner = createTestOwner();
        HistoryOwner historyOwner = new HistoryOwner();
        historyOwner.setCar(car);
        historyOwner.setOwner(owner);
        return historyOwner;
    }

    private Car createTestCar() {
        Car car = new Car();
        car.setName("Car");
        new HbmCarRepository(crudRepository).create(car);
        return car;
    }

    private Owner createTestOwner() {
        Owner owner = new Owner();
        owner.setName("Owner");
        new HbmOwnerRepository(crudRepository).create(owner);
        return owner;
    }
}