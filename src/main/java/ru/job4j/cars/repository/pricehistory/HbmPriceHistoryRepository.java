package ru.job4j.cars.repository.pricehistory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmPriceHistoryRepository implements PriceHistoryRepository {

    private final CrudRepository crudRepository;

    @Override
    public PriceHistory create(PriceHistory priceHistory) {
        crudRepository.run(session -> session.persist(priceHistory));
        return priceHistory;
    }

    @Override
    public void update(PriceHistory priceHistory) {
        crudRepository.run(session -> session.merge(priceHistory));
    }

    @Override
    public void delete(int priceHistoryId) {
        crudRepository.run(
                "delete from PriceHistory where id = :fId",
                Map.of("fId", priceHistoryId)
        );
    }

    @Override
    public List<PriceHistory> findAllOrderById() {
        return crudRepository.query("from PriceHistory order by id asc", PriceHistory.class);
    }

    @Override
    public Optional<PriceHistory> findById(int priceHistoryId) {
        return crudRepository.optional(
                "from PriceHistory where id = :fId", PriceHistory.class,
                Map.of("fId", priceHistoryId)
        );
    }
}
