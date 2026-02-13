package com.ip.processservice.dto.request;

import lombok.Data;

@Data
public class ProcessRequest {
    private String jobId;
    private String imageId;
    private String userId;
    private String action;

    // --- Thông số cho Resize ---
    private Integer width;
    private Integer height;
    private Double scale;

    // --- Thông số cho Crop ---
    private Integer cropX;
    private Integer cropY;
    private Integer cropWidth;
    private Integer cropHeight;

    // --- Thông số cho Compress/Convert ---
    private String targetFormat;
    private Double quality;

    // --- Thông số cho Watermark ---
   // private String watermarkText;
    // Hoặc watermarkImageId nếu chèn logo
}
