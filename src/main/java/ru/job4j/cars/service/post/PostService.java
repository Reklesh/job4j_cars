package ru.job4j.cars.service.post;

import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post create(Post post);

    void update(Post post);

    void delete(int postId);

    List<Post> findAllOrderById();

    Optional<Post> findById(int postId);
}
