package ru.job4j.cars.service.historyowner;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.HistoryOwner;
import ru.job4j.cars.repository.historyowner.HistoryOwnerRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleHistoryOwnerService implements HistoryOwnerService {

    private final HistoryOwnerRepository historyOwnerRepository;

    @Override
    public HistoryOwner create(HistoryOwner historyOwner) {
        return historyOwnerRepository.create(historyOwner);
    }

    @Override
    public void update(HistoryOwner historyOwner) {
        historyOwnerRepository.update(historyOwner);
    }

    @Override
    public void delete(int historyOwnerId) {
        historyOwnerRepository.delete(historyOwnerId);
    }

    @Override
    public List<HistoryOwner> findAllOrderById() {
        return historyOwnerRepository.findAllOrderById();
    }

    @Override
    public Optional<HistoryOwner> findById(int historyOwnerId) {
        return historyOwnerRepository.findById(historyOwnerId);
    }
}
