package ru.job4j.cars.service.post;

import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post create(Post post, List<FileDto> images);

    void update(Post post, List<FileDto> images);

    boolean delete(int postId);

    List<Post> findAllOrderById();

    Optional<Post> findById(int postId);

    List<Post> findByCreatedAfter();

    List<Post> findAllWithPhotos();

    List<Post> findByLikeCarBrand(String key);
}
