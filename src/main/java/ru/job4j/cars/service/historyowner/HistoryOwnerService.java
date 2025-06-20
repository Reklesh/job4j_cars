package ru.job4j.cars.service.historyowner;

import ru.job4j.cars.model.HistoryOwner;

import java.util.List;
import java.util.Optional;

public interface HistoryOwnerService {

    HistoryOwner create(HistoryOwner historyOwner);

    void update(HistoryOwner historyOwner);

    void delete(int historyOwnerId);

    List<HistoryOwner> findAllOrderById();

    Optional<HistoryOwner> findById(int historyOwnerId);
}
