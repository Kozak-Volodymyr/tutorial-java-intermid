package com.example.restaurantDemo.service.impl;

import com.example.restaurantDemo.exceptions.StorageException;
import com.example.restaurantDemo.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@Slf4j
public class FileSystemStorageService  implements StorageService {

    @Value("${app.storage.location:upload}")
    private String storageLocation;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not create directory root location: " + storageLocation, e);
        }
    }

    @Override
    public String store(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("File cannot be empty");
            }
            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String finalFileName = filename + "." + extension;
            Path destinatedFile = rootLocation
                    .resolve(Paths.get(finalFileName))
                    .normalize()
                    .toAbsolutePath();

            if (!destinatedFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store files outside of the specified directory");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinatedFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return finalFileName;
        } catch (IOException e) {
            throw new StorageException("Could not store file " + filename + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Resource> loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            UrlResource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return Optional.of(resource);
            } else {
                return Optional.empty();
            }
        } catch (MalformedURLException e) {
            log.warn("Could not read file: %s".formatted(filename), e);
            return Optional.empty();
        }
    }
}
