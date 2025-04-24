package ru.job4j.cars.repository.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmPostRepository implements PostRepository {

    private final CrudRepository crudRepository;

    @Override
    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    @Override
    public void update(Post post) {
        crudRepository.run(session -> session.merge(post));
    }

    @Override
    public boolean delete(int postId) {
        return crudRepository.execute(
                "delete Post where id = :fId",
                Map.of("fId", postId));
    }

    @Override
    public List<Post> findAllOrderById() {
        return crudRepository.query(
                """
                        SELECT DISTINCT p FROM Post p
                        JOIN FETCH p.priceHistories
                        JOIN FETCH p.participates
                        JOIN FETCH p.photos
                        ORDER BY id ASC
                        """,
                Post.class);
    }

    @Override
    public Optional<Post> findById(int postId) {
        return crudRepository.optional(
                """
                        SELECT DISTINCT p FROM Post p
                        JOIN FETCH p.priceHistories
                        JOIN FETCH p.participates
                        JOIN FETCH p.photos
                        WHERE id = :fId
                        """,
                Post.class,
                Map.of("fId", postId));
    }

    @Override
    public List<Post> findByCreatedAfter() {
        return crudRepository.query(
                """
                        SELECT DISTINCT p FROM Post p
                        JOIN FETCH p.priceHistories
                        JOIN FETCH p.participates
                        JOIN FETCH p.photos
                        WHERE created > :fCreated
                        ORDER BY p.id
                        """,
                Post.class,
                Map.of("fCreated", LocalDateTime.now().minusDays(1)));
    }

    @Override
    public List<Post> findAllWithPhotos() {
        return crudRepository.query(
                """
                        SELECT DISTINCT p FROM Post p
                        JOIN FETCH p.priceHistories
                        JOIN FETCH p.participates
                        JOIN FETCH p.photos
                        WHERE SIZE(p.photos) > 0
                        ORDER BY p.id
                        """,
                Post.class);
    }

    @Override
    public List<Post> findByLikeCarBrand(String key) {
        return crudRepository.query(
                """
                        SELECT DISTINCT p FROM Post p
                        JOIN FETCH p.priceHistories
                        JOIN FETCH p.participates
                        JOIN FETCH p.photos
                        WHERE p.car.name like :fKey
                        """,
                Post.class,
                Map.of("fKey", "%" + key + "%"));
    }
}
