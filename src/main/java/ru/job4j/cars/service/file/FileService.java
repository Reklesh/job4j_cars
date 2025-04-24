package ru.job4j.cars.service.file;

import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.File;

import java.util.List;
import java.util.Optional;

public interface FileService {

    File save(FileDto fileDto);

    Optional<FileDto> getFileById(int id);

    List<FileDto> getAllFileByIds(List<Integer> fileIds);

    void deleteById(int id);

    void deleteAllByIds(List<Integer> fileIds);
}