package ru.job4j.cars.repository.post;

import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post create(Post post);

    void update(Post post);

    boolean delete(int postId);

    List<Post> findAllOrderById();

    Optional<Post> findById(int postId);

    List<Post> findByCreatedAfter();

    List<Post> findAllWithPhotos();

    List<Post> findByLikeCarBrand(String key);
}
