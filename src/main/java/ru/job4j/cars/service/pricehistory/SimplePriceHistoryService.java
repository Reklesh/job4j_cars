package ru.job4j.cars.service.pricehistory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.pricehistory.PriceHistoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimplePriceHistoryService implements PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    @Override
    public PriceHistory create(PriceHistory priceHistory) {
        return priceHistoryRepository.create(priceHistory);
    }

    @Override
    public void update(PriceHistory priceHistory) {
        priceHistoryRepository.update(priceHistory);
    }

    @Override
    public void delete(int priceHistoryId) {
        priceHistoryRepository.delete(priceHistoryId);
    }

    @Override
    public List<PriceHistory> findAllOrderById() {
        return priceHistoryRepository.findAllOrderById();
    }

    @Override
    public Optional<PriceHistory> findById(int priceHistoryId) {
        return priceHistoryRepository.findById(priceHistoryId);
    }
}
