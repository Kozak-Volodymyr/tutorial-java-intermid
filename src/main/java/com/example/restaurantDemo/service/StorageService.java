package com.example.restaurantDemo.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public interface StorageService {
    String store(MultipartFile file, String filename);
    Optional<Resource> loadAsResource(String id);
}
