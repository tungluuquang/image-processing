package com.ip.processservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessResponse {
    private String downloadUrl;   // Link tải ảnh: http://localhost:9000/...
    private String imageId;       // ID mới của ảnh sau khi xử lý (để FE track)
    private String format;        // Định dạng ảnh: png, jpg...
    private long size;            // Kích thước file (byte) - Optional, có thì càng tốt
}