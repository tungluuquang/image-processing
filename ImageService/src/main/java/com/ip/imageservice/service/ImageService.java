package com.ip.imageservice.service;

import com.ip.imageservice.model.Image;
import com.ip.imageservice.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepo;
    private final MinioService minioService;

    public Image upload(MultipartFile file) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        String fileName = minioService.uploadFile(file);
        String fileUrl = minioService.getPublicUrl(fileName);

        Image image = new Image();
        image.setFileName(fileName);
        image.setFileUrl(fileUrl);
        image.setOwnerId(currentUserId);
        image.setSize(file.getSize());

        return imageRepo.save(image);
    }

    public void deleteImage(String imageId) {
        Image image = imageRepo.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Unexisted image file"));

        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!image.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("Do not have rights to delete this image");
        }
        minioService.deleteFile(image.getFileName());

        imageRepo.delete(image);
    }

    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File cannot null");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only accept image file format (JPG, PNG, GIF...)");
        }

        // 3. (Nâng cao) Check Magic Number (Byte đầu của file) để chắc chắn là ảnh thật
        // Dùng thư viện Apache Tika nếu muốn kỹ hơn.
    }

    public Image getImageDetail(String imageId) {
        return imageRepo.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Ảnh không tồn tại"));
    }

    public List<Image> getMyImages() {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        return imageRepo.findAllByOwnerId(currentUserId);
    }
}
