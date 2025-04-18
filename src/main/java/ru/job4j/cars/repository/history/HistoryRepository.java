package ru.job4j.cars.repository.history;

import ru.job4j.cars.model.History;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository {

    History create(History history);

    void update(History history);

    void delete(int historyId);

    List<History> findAllOrderById();

    Optional<History> findById(int historyId);
}
