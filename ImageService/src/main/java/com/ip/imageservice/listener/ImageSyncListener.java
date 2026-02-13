package com.ip.imageservice.listener;

import com.ip.imageservice.config.RabbitMQConfig;
import com.ip.imageservice.dto.event.ImageProcessedEvent;
import com.ip.imageservice.model.Image;
import com.ip.imageservice.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageSyncListener {

    private final ImageRepository imageRepository;

    @RabbitListener(queues = RabbitMQConfig.SYNC_QUEUE)
    public void receiveMessage(ImageProcessedEvent event) {
        log.info("Receive events from ProcessService: {}", event.getNewImageId());

        try {
            if (imageRepository.existsByFileName(event.getNewImageId())) {
                log.warn("Image {} has existed, by-pass", event.getNewImageId());
                return;
            }

            Image newImage = new Image();
            newImage.setOwnerId(event.getUserId());
            newImage.setFileName(event.getNewImageId());
            newImage.setFileUrl(event.getDownloadUrl());
            newImage.setOriginalImageId(event.getOriginalImageId());

            newImage.setType("PROCESSED");
            newImage.setSize(event.getSize());
            newImage.setFormat(event.getFormat());

            imageRepository.save(newImage);

            log.info("Save successfully image to db!");

        } catch (Exception e) {
            log.error("Error when saving img to db: {}", e.getMessage());
        }
    }
}