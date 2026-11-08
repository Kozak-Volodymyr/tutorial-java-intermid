package com.example.restaurantDemo.controllers;

import com.example.restaurantDemo.domain.dto.PhotoDto;
import com.example.restaurantDemo.domain.entities.Photo;
import com.example.restaurantDemo.mappers.PhotoMapper;
import com.example.restaurantDemo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/photos")
public class PhotoController {
    private final PhotoMapper photoMapper;
    private final PhotoService photoService;

    @PostMapping
    public PhotoDto uploadPhoto(@RequestParam("file") MultipartFile file) {
        Photo savedPhoto=photoService.uploadPhoto(file);
        return photoMapper.toDto(savedPhoto);
    }

    @GetMapping(path="/{id:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String id) {
        return photoService.getPhotoAsResource(id).map(photo->
            ResponseEntity.ok().contentType(MediaTypeFactory.getMediaType(photo).orElse(MediaType.APPLICATION_OCTET_STREAM)
                    )
                    .header(HttpHeaders.CONTENT_DISPOSITION,"inline")
                    .body(photo)).orElse(ResponseEntity.notFound().build());

    }

}
