package com.ip.imageservice.controller;

import com.ip.imageservice.model.Image;
import com.ip.imageservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Image> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(imageService.upload(file));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable String imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<Image> getImageDetail(@PathVariable String imageId) {
        return ResponseEntity.ok(imageService.getImageDetail(imageId));
    }

    @GetMapping("/my-gallery")
    public ResponseEntity<List<Image>> getMyImages() {
        return ResponseEntity.ok(imageService.getMyImages());
    }
}