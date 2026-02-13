package com.ip.processservice.listener;

import com.ip.processservice.config.RabbitMQConfig;
import com.ip.processservice.dto.event.ImageProcessedEvent;
import com.ip.processservice.dto.request.ProcessRequest;
import com.ip.processservice.dto.response.ProcessResponse;
import com.ip.processservice.service.JobService;
import com.ip.processservice.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessListener {

    private final ProcessService processingService;
    private final JobService jobService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(ProcessRequest request) {
        log.info("Worker receives task: {}", request.getJobId());

        try {
            Thread.sleep(5000);
            ProcessResponse result = processingService.execute(request);
            jobService.completeJob(request.getJobId(), result);
            if (request.getUserId() != null && !request.getUserId().isEmpty()) {
                // CASE 1: LÀ USER ĐĂNG NHẬP
                // -> Bắn event để ImageService lưu vào lịch sử
                ImageProcessedEvent event = new ImageProcessedEvent (
                        request.getUserId(),
                        request.getImageId(),
                        result.getImageId(),
                        result.getDownloadUrl(),
                        result.getSize(),
                        result.getFormat()
                );
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.SYNC_EXCHANGE,
                        RabbitMQConfig.SYNC_ROUTING_KEY,
                        event
                );
                log.info("Save event to db's user: {}", request.getUserId());

            } else {
                log.info("Guest's job! Do nothing on db.");
            }
        } catch (Exception e) {
            jobService.failJob(request.getJobId(), e.getMessage());
        }
    }
}