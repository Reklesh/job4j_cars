package ru.job4j.cars.repository.car;

import org.hibernate.SessionFactory;
import org.hibernate.TransientObjectException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.HistoryOwner;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.repository.CrudRepository;
import ru.job4j.cars.repository.engine.HbmEngineRepository;
import ru.job4j.cars.repository.historyowner.HbmHistoryOwnerRepository;
import ru.job4j.cars.repository.owner.HbmOwnerRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HbmCarRepositoryTest {

    private HbmCarRepository repository;
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
        repository = new HbmCarRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
    }

    @Test
    void whenAddNewCarThenFindByIdReturnsSameCar() {
        Car car = new Car();
        car.setName("test");
        repository.create(car);

        Car result = repository.findById(car.getId()).orElseThrow();

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getEngine()).isNull();
        assertThat(result.getOwner()).isNull();
        assertThat(result.getHistoryOwners()).isEmpty();
    }

    @Test
    void whenAddNewCarWithAllDependenciesThenFindByIdReturnsSameCar() {
        Engine engine = new Engine();
        engine.setName("Engine");
        new HbmEngineRepository(crudRepository).create(engine);

        Owner owner = new Owner();
        owner.setName("Owner");
        new HbmOwnerRepository(crudRepository).create(owner);

        Car car = new Car();
        car.setName("Car");
        car.setEngine(engine);
        car.setOwner(owner);
        repository.create(car);

        HistoryOwner historyOwner = new HistoryOwner();
        historyOwner.setCar(car);
        historyOwner.setOwner(owner);
        new HbmHistoryOwnerRepository(crudRepository).create(historyOwner);

        Car result = repository.findById(car.getId()).orElseThrow();

        assertThat(result.getName()).isEqualTo("Car");
        assertThat(result.getEngine().getId()).isEqualTo(engine.getId());
        assertThat(result.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(result.getHistoryOwners())
                .hasSize(1)
                .first()
                .extracting(
                        HistoryOwner::getId,
                        hOwner -> hOwner.getOwner().getName())
                .containsExactly(historyOwner.getId(), "Owner");
    }

    @Test
    public void whenAddCarWithUnsavedEngineThenCarNotSaved() {
        Car car = new Car();
        car.setName("test");
        Engine engine = new Engine();
        engine.setName("test2");
        car.setEngine(engine);

        assertThatThrownBy(() -> repository.create(car))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(TransientObjectException.class)
                .hasMessageContaining("save the transient instance before flushing");

    }

    @Test
    public void whenTestFindAll() {
        Car first = new Car();
        first.setName("First");
        Car second = new Car();
        second.setName("Second");
        repository.create(first);
        repository.create(second);

        Car result = repository.findAllOrderById().getFirst();

        assertThat(result).isEqualTo(first);
        assertThat(result.getEngine()).isNull();
        assertThat(result.getOwner()).isNull();
        assertThat(result.getHistoryOwners()).isEmpty();
    }

    @Test
    public void whenReplaceCarIsSuccessful() {
        Car car = new Car();
        car.setName("Bug");
        repository.create(car);
        int id = car.getId();
        Car updateCar = new Car();
        updateCar.setId(id);
        updateCar.setName("Bug with description");
        repository.update(updateCar);

        assertThat(repository.findById(id).orElseThrow().getName())
                .isEqualTo("Bug with description");
    }

    @Test
    public void whenReplaceCarIsNotSuccessful() {
        Car car = new Car();
        car.setName("Bug");
        repository.create(car);
        Car updateCar = new Car();
        updateCar.setName("Bug with description");
        repository.update(updateCar);

        assertThat(repository.findById(car.getId()).orElseThrow().getName()).isEqualTo("Bug");
    }

    @Test
    public void whenDeleteCarIsSuccessful() {
        Car car = new Car();
        car.setName("test");
        repository.create(car);
        int id = car.getId();
        repository.delete(id);

        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    public void whenDeleteCarIsNotSuccessful() {
        Car car = new Car();
        car.setName("test");
        repository.create(car);
        repository.delete(1000);

        assertThat(repository.findById(car.getId()).orElseThrow()).isEqualTo(car);
    }
}