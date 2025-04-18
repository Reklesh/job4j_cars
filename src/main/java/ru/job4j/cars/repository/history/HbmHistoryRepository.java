package ru.job4j.cars.repository.history;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.History;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmHistoryRepository implements HistoryRepository {

    private final CrudRepository crudRepository;

    @Override
    public History create(History history) {
        crudRepository.run(session -> session.persist(history));
        return history;
    }

    @Override
    public void update(History history) {
        crudRepository.run(session -> session.merge(history));
    }

    @Override
    public void delete(int historyId) {
        crudRepository.run(
                "delete from History where id = :fId",
                Map.of("fId", historyId)
        );
    }

    @Override
    public List<History> findAllOrderById() {
        return crudRepository.query("from History order by id asc", History.class);
    }

    @Override
    public Optional<History> findById(int historyId) {
        return crudRepository.optional(
                "from History where id = :fId", History.class,
                Map.of("fId", historyId)
        );
    }
}
