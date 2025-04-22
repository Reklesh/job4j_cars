package ru.job4j.cars.repository.historyowner;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.HistoryOwner;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmHistoryOwnerRepository implements HistoryOwnerRepository {

    private final CrudRepository crudRepository;

    @Override
    public HistoryOwner create(HistoryOwner historyOwner) {
        crudRepository.run(session -> session.persist(historyOwner));
        return historyOwner;
    }

    @Override
    public void update(HistoryOwner historyOwner) {
        crudRepository.run(session -> session.merge(historyOwner));
    }

    @Override
    public void delete(int historyOwnerId) {
        crudRepository.run(
                "delete from HistoryOwner where id = :fId",
                Map.of("fId", historyOwnerId)
        );
    }

    @Override
    public List<HistoryOwner> findAllOrderById() {
        return crudRepository.query("from HistoryOwner order by id asc", HistoryOwner.class);
    }

    @Override
    public Optional<HistoryOwner> findById(int historyOwnerId) {
        return crudRepository.optional(
                "from HistoryOwner where id = :fId", HistoryOwner.class,
                Map.of("fId", historyOwnerId)
        );
    }
}
