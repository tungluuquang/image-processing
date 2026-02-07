package com.ip.processservice.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucket-input}")
    private String bucketInput;

    @Value("${minio.bucket-output}")
    private String bucketOutput;

    @Value("${minio.public-endpoint}")
    private String publicEndpoint;

    public InputStream downloadFile(String fileName) {
        try {
            log.info("Downloading file from bucket: {}/{}", bucketInput, fileName);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketInput)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage());
            throw new RuntimeException("Cannot find source-file: " + fileName);
        }
    }

    public void uploadFile(String fileName, InputStream stream, long size, String contentType) {
        try {
            log.info("Uploading file to bucket: {}/{}", bucketOutput, fileName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketOutput)
                            .object(fileName)
                            .stream(stream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when uploading file: {}", e.getMessage());
            throw new RuntimeException("Error saving manufactured files");
        }
    }

    public void deleteProcessedFile(String fileName) {
        try {
            log.info("Deleting file from bucket: {}/{}", bucketOutput, fileName);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketOutput)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting image from MinIO: " + e.getMessage());
        }
    }

    public String getPublicUrl(String fileName) {
        String host = publicEndpoint.endsWith("/") ? publicEndpoint : publicEndpoint + "/";
        return host + bucketOutput + "/" + fileName;
    }
}