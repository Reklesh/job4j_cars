package ru.job4j.cars.repository.file;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.File;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HbmFileRepositoryTest {

    private HbmFileRepository repository;
    private SessionFactory sf;

    @BeforeEach
    void init() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();

        sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();

        repository = new HbmFileRepository(new CrudRepository(sf));
    }

    @AfterEach
    void tearDown() {
        if (sf != null) {
            sf.close();
        }
    }

    @Test
    void whenSaveFileThenFindByIdReturnsSameFile() {
        File file = new File("photo.png", "files/photo.png");
        repository.save(file);

        File result = repository.findById(file.getId()).orElseThrow();

        assertThat(result.getName()).isEqualTo("photo.png");
        assertThat(result.getPath()).isEqualTo("files/photo.png");
    }

    @Test
    void whenSaveMultipleFilesThenFindAllByIdsReturnsCorrectFiles() {
        File file1 = repository.save(new File("photo1.png", "files/photo1.png"));
        repository.save(new File("photo2.png", "files/photo2.png"));
        File file3 = repository.save(new File("photo3.png", "files/photo3.png"));

        List<File> result = repository.findAllByIds(List.of(file1.getId(), file3.getId()));

        assertThat(result)
                .hasSize(2)
                .extracting(File::getName)
                .containsExactlyInAnyOrder("photo1.png", "photo3.png");
    }

    @Test
    void whenDeleteByIdThenFileNotFound() {
        File file = repository.save(new File("photo.png", "files/photo.png"));
        int id = file.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void whenDeleteAllByIdsThenFilesNotFound() {
        File file1 = repository.save(new File("photo1.png", "files/photo1.png"));
        File file2 = repository.save(new File("photo2.png", "files/photo2.png"));

        repository.deleteAllByIds(List.of(file1.getId(), file2.getId()));

        assertThat(repository.findById(file1.getId())).isEmpty();
        assertThat(repository.findById(file2.getId())).isEmpty();
    }

    @Test
    void whenFindByIdNotExistsThenReturnEmpty() {
        assertThat(repository.findById(1000)).isEmpty();
    }

    @Test
    void whenFindAllByIdsEmptyListThenReturnEmptyList() {
        repository.save(new File("photo.png", "files/photo.png"));
        assertThat(repository.findAllByIds(List.of())).isEmpty();
    }
}