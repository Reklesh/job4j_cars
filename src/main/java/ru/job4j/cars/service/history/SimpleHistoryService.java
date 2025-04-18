package ru.job4j.cars.service.history;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.History;
import ru.job4j.cars.repository.history.HistoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleHistoryService implements HistoryService {

    private final HistoryRepository historyRepository;

    @Override
    public History create(History history) {
        return historyRepository.create(history);
    }

    @Override
    public void update(History history) {
        historyRepository.update(history);
    }

    @Override
    public void delete(int historyId) {
        historyRepository.delete(historyId);
    }

    @Override
    public List<History> findAllOrderById() {
        return historyRepository.findAllOrderById();
    }

    @Override
    public Optional<History> findById(int historyId) {
        return historyRepository.findById(historyId);
    }
}
