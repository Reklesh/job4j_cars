package ru.job4j.cars.service.pricehistory;

import ru.job4j.cars.model.PriceHistory;

import java.util.List;
import java.util.Optional;

public interface PriceHistoryService {

    PriceHistory create(PriceHistory priceHistory);

    void update(PriceHistory priceHistory);

    void delete(int priceHistoryId);

    List<PriceHistory> findAllOrderById();

    Optional<PriceHistory> findById(int priceHistoryId);
}
