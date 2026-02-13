package com.ip.imageservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fileName;
    private String fileUrl;

    private String ownerId;

    private Long size;
    private String format;
    private String type;
    private String originalImageId;

    @Column(updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
        if (this.type == null) this.type = "ORIGINAL";
    }
}