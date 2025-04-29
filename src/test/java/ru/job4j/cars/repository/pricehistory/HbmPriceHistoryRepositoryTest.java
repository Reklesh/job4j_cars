package ru.job4j.cars.repository.pricehistory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HbmPriceHistoryRepositoryTest {

    private HbmPriceHistoryRepository repository;
    private SessionFactory sf;

    @BeforeEach
    void init() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();

        sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();

        repository = new HbmPriceHistoryRepository(new CrudRepository(sf));
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
    }

    @Test
    void whenCreatePriceHistoryThenFindByIdReturnsIt() {
        PriceHistory history = new PriceHistory();
        history.setBefore(1000);
        history.setAfter(2000);
        repository.create(history);

        PriceHistory result = repository.findById(history.getId()).orElseThrow();

        assertThat(result.getBefore()).isEqualTo(1000);
        assertThat(result.getAfter()).isEqualTo(2000);
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void whenUpdatePriceHistoryThenFindByIdReturnsUpdated() {
        PriceHistory history = new PriceHistory();
        history.setBefore(1000);
        repository.create(history);

        history.setAfter(2000);
        repository.update(history);

        PriceHistory result = repository.findById(history.getId()).orElseThrow();

        assertThat(result.getAfter()).isEqualTo(2000);
    }

    @Test
    void whenDeletePriceHistoryThenFindByIdReturnsEmpty() {
        PriceHistory history = new PriceHistory();
        repository.create(history);
        int id = history.getId();

        repository.delete(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void whenFindAllOrderByIdThenReturnsAllInOrder() {
        PriceHistory first = new PriceHistory();
        first.setBefore(1000);
        repository.create(first);

        PriceHistory second = new PriceHistory();
        second.setBefore(2000);
        repository.create(second);

        List<PriceHistory> result = repository.findAllOrderById();

        assertThat(result)
                .hasSize(2)
                .extracting(PriceHistory::getBefore)
                .containsExactly(1000L, 2000L);
    }

    @Test
    void whenCreatePriceHistoryWithAllFieldsThenAllFieldsSaved() {
        LocalDateTime testTime = LocalDateTime.now().minusDays(1).withNano(0);
        PriceHistory history = new PriceHistory();
        history.setBefore(1000);
        history.setAfter(2000);
        history.setCreated(testTime);
        repository.create(history);

        PriceHistory result = repository.findById(history.getId()).orElseThrow();

        assertThat(result)
                .extracting(
                        PriceHistory::getBefore,
                        PriceHistory::getAfter,
                        PriceHistory::getCreated
                )
                .containsExactly(1000L, 2000L, testTime);
    }
}