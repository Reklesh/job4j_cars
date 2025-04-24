package ru.job4j.cars.repository.file;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.File;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmFileRepository implements FileRepository {

    private final CrudRepository crudRepository;

    @Override
    public File save(File file) {
        crudRepository.run(session -> session.persist(file));
        return file;
    }

    @Override
    public Optional<File> findById(int id) {
        return crudRepository.optional(
                "from File where id = :fId", File.class,
                Map.of("fId", id)
        );
    }

    @Override
    public List<File> findAllByIds(List<Integer> fileIds) {
        return crudRepository.query(
                "from File WHERE id IN (:fFileIds) ORDER BY id", File.class,
                Map.of("fFileIds", fileIds));
    }

    @Override
    public void deleteById(int id) {
        crudRepository.run(
                "delete from File where id = :fId",
                Map.of("fId", id)
        );
    }

    @Override
    public void deleteAllByIds(List<Integer> fileIds) {
        crudRepository.run(
                "delete from File where id IN (:fFileIds)",
                Map.of("fFileIds", fileIds)
        );
    }
}
