package com.ip.processservice.service;

import io.minio.*;
import io.minio.messages.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

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

    @PostConstruct
    public void init() {
        configureLifecycle();
    }

    public void configureLifecycle() {
        try {
            List<LifecycleRule> rules = new LinkedList<>();

            rules.add(new LifecycleRule(
                    Status.ENABLED,
                    null,
                    new Expiration((ZonedDateTime) null, 1, null),
                    new RuleFilter("guest/"),
                    "expire-guest-images-rule", // Tên luật (ID)
                    null, null, null
            ));

            LifecycleConfiguration config = new LifecycleConfiguration(rules);

            minioClient.setBucketLifecycle(
                    SetBucketLifecycleArgs.builder()
                            .bucket(bucketOutput)
                            .config(config)
                            .build()
            );

            log.info("Saving LifeCycle configuration: Delete automatically guests photos after a day.");

        } catch (Exception e) {
            log.error("Error Lifecycle MinIO configuration: {}", e.getMessage());
        }
    }
}