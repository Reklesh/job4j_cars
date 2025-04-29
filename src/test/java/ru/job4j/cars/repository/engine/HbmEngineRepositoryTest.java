package ru.job4j.cars.repository.engine;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.CrudRepository;

import static org.assertj.core.api.Assertions.assertThat;

class HbmEngineRepositoryTest {

    private HbmEngineRepository repository;
    private SessionFactory sf;

    @BeforeEach
    void init() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();

        sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();

        repository = new HbmEngineRepository(new CrudRepository(sf));
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
    }

    @Test
    void whenAddNewEngineThenFindByIdReturnsSameEngine() {
        Engine engine = new Engine();
        engine.setName("test");
        repository.create(engine);

        Engine result = repository.findById(engine.getId()).orElseThrow();

        assertThat(result.getName()).isEqualTo(engine.getName());
    }

    @Test
    void whenTestFindAll() {
        Engine first = new Engine();
        first.setName("First");
        Engine second = new Engine();
        second.setName("Second");
        repository.create(first);
        repository.create(second);

        Engine result = repository.findAllOrderById().getFirst();

        assertThat(result).isEqualTo(first);
    }

    @Test
    void whenReplaceEngineIsSuccessful() {
        Engine engine = new Engine();
        engine.setName("V8");
        repository.create(engine);
        int id = engine.getId();
        Engine updateEngine = new Engine();
        updateEngine.setId(id);
        updateEngine.setName("V8 Turbo");

        repository.update(updateEngine);

        assertThat(repository.findById(id).orElseThrow().getName())
                .isEqualTo("V8 Turbo");
    }

    @Test
    void whenReplaceEngineIsNotSuccessful() {
        Engine engine = new Engine();
        engine.setName("V8");
        repository.create(engine);
        Engine updateEngine = new Engine();
        updateEngine.setName("V8 Turbo");

        repository.update(updateEngine);

        assertThat(repository.findById(engine.getId()).orElseThrow().getName())
                .isEqualTo("V8");
    }

    @Test
    void whenDeleteEngineIsSuccessful() {
        Engine engine = new Engine();
        engine.setName("test");
        repository.create(engine);
        int id = engine.getId();

        repository.delete(id);

        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    void whenDeleteEngineIsNotSuccessful() {
        Engine engine = new Engine();
        engine.setName("test");

        repository.create(engine);
        repository.delete(1000);

        assertThat(repository.findById(engine.getId()).orElseThrow())
                .isEqualTo(engine);
    }
}