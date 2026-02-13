package com.ip.processservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageProcessedEvent {
    private String userId;
    private String originalImageId;
    private String newImageId;
    private String downloadUrl;
    private Long size;
    private String format;
}