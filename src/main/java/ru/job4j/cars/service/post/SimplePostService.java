package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.service.file.FileService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SimplePostService implements PostService {

    private final PostRepository postRepository;
    private final FileService fileService;

    @Override
    public Post create(Post post, List<FileDto> images) {
        var photos = saveNewFile(images);
        post.setPhotos(photos);
        return postRepository.create(post);
    }

    private Set<File> saveNewFile(List<FileDto> images) {
        return images.stream()
                .map(fileService::save)
                .collect(Collectors.toSet());
    }

    @Override
    public void update(Post post, List<FileDto> images) {
        boolean isAllNewFilesEmpty = images.stream()
                .allMatch(image -> image.getContent().length == 0);
        if (isAllNewFilesEmpty) {
            postRepository.update(post);
            return;
        }
        var newPhotos = saveNewFile(images);
        deleteFile(post);
        post.setPhotos(newPhotos);
        postRepository.update(post);
    }

    private void deleteFile(Post post) {
        List<Integer> fileIds = post.getPhotos().stream()
                .map(File::getId)
                .toList();
        fileService.deleteAllByIds(fileIds);
    }

    @Override
    public boolean delete(int postId) {
        var postOptional = findById(postId);
        boolean isDeleted = false;
        if (postOptional.isPresent()) {
            isDeleted = postRepository.delete(postId);
            deleteFile(postOptional.get());
        }
        return isDeleted;
    }

    @Override
    public List<Post> findAllOrderById() {
        return postRepository.findAllOrderById();
    }

    @Override
    public Optional<Post> findById(int postId) {
        return postRepository.findById(postId);
    }

    @Override
    public List<Post> findByCreatedAfter() {
        return postRepository.findByCreatedAfter();
    }

    @Override
    public List<Post> findAllWithPhotos() {
        return postRepository.findAllWithPhotos();
    }

    @Override
    public List<Post> findByLikeCarBrand(String key) {
        return postRepository.findByLikeCarBrand(key);
    }
}
