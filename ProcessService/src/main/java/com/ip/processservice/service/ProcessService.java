package com.ip.processservice.service;

import com.ip.processservice.dto.request.ProcessRequest;
import com.ip.processservice.dto.response.ProcessResponse;
import com.ip.processservice.operation.ImageOperation;
import com.ip.processservice.pattern.OperationFactory;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessService {
    private final MinioService minioService;
    private final OperationFactory operationFactory;

    public ProcessResponse execute(ProcessRequest request) {
        try {
            // --- BƯỚC 1: TẢI ẢNH GỐC ---
            InputStream originalImage = minioService.downloadFile(request.getImageId());

            // --- BƯỚC 2: CHUẨN BỊ XỬ LÝ ---
            Thumbnails.Builder<? extends InputStream> builder = Thumbnails.of(originalImage);

            // Lấy danh sách lệnh từ Factory
            List<ImageOperation> operations = operationFactory.getOperations(request);

            // Áp dụng từng lệnh (Resize, Crop...)
            for (ImageOperation op : operations) {
                op.apply(builder);
            }

            // --- BƯỚC 3: XUẤT RA RAM ĐỂ LẤY KÍCH THƯỚC ---
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            builder.toOutputStream(outputStream); // Lúc này Thumbnailator mới thực sự chạy

            // Chuyển thành mảng byte để tính dung lượng (Fix lỗi thiếu size)
            byte[] imageBytes = outputStream.toByteArray();
            long size = imageBytes.length;

            // --- BƯỚC 4: UPLOAD LÊN MINIO ---
            InputStream processedStream = new ByteArrayInputStream(imageBytes);
            String ext = request.getTargetFormat() != null ? request.getTargetFormat() : "jpg";
            String newFileName = UUID.randomUUID() + "." + ext;

            // Gọi hàm upload với đủ 4 tham số (đã fix)
            minioService.uploadFile(
                    newFileName,
                    processedStream,
                    size,
                    "image/" + ext
            );

            // Lấy URL công khai
            String publicUrl = minioService.getPublicUrl(newFileName);

            // --- BƯỚC 5: TRẢ VỀ OBJECT JSON (Thay vì chỉ trả về String) ---
            return new ProcessResponse(
                    publicUrl,      // Link tải
                    newFileName,    // ID mới
                    ext,            // Định dạng
                    size            // Kích thước file
            );

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý: " + e.getMessage());
        }
    }
}
