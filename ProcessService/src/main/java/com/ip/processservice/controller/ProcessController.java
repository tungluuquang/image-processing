package com.ip.processservice.controller;

import com.ip.processservice.config.RabbitMQConfig;
import com.ip.processservice.dto.event.JobStatusEvent;
import com.ip.processservice.dto.request.ProcessRequest;
import com.ip.processservice.dto.response.ProcessResponse;
import com.ip.processservice.service.JobService;
import com.ip.processservice.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/process")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;
    private final RabbitTemplate rabbitTemplate;
    private final JobService jobService;

    @PostMapping
    public ResponseEntity<ProcessResponse> processImage(@RequestBody ProcessRequest request) {
        ProcessResponse response = processService.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/async")
    public ResponseEntity<Map<String, String>> processImageAsync(@RequestBody ProcessRequest request) {
        String jobId = UUID.randomUUID().toString();
        request.setJobId(jobId);
        jobService.initJob(jobId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                request
        );

        return ResponseEntity.accepted().body(Map.of(
                "message", "Request accepted. Processing in background.",
                "jobId", jobId,
                "statusUrl", "/api/v1/process/status/" + jobId
        ));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobStatusEvent> checkStatus(@PathVariable String jobId) {
        JobStatusEvent status = jobService.getJobStatus(jobId);

        if ("NOT_FOUND".equals(status.getStatus())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(status);
    }
}
