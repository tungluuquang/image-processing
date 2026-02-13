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
            InputStream originalImage = minioService.downloadFile(request.getImageId());

            Thumbnails.Builder<? extends InputStream> builder = Thumbnails.of(originalImage);

            List<ImageOperation> operations = operationFactory.getOperations(request);

            for (ImageOperation op : operations) {
                op.apply(builder);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            builder.toOutputStream(outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            long size = imageBytes.length;

            InputStream processedStream = new ByteArrayInputStream(imageBytes);
            String ext = request.getTargetFormat() != null ? request.getTargetFormat() : "jpg";
            String newFileName = UUID.randomUUID() + "." + ext;

            minioService.uploadFile(
                    newFileName,
                    processedStream,
                    size,
                    "image/" + ext
            );

            String publicUrl = minioService.getPublicUrl(newFileName);

            return new ProcessResponse(
                    publicUrl,
                    newFileName,
                    ext,
                    size
            );

        } catch (Exception e) {
            throw new RuntimeException("Error processing: " + e.getMessage());
        }
    }
}
