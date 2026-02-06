package com.ip.imageservice.repository;

import com.ip.imageservice.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {
    List<Image> findAllByOwnerId(String ownerId);
}
