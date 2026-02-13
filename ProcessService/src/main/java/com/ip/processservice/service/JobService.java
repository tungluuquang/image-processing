package com.ip.processservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ip.processservice.dto.event.JobStatusEvent;
import com.ip.processservice.dto.response.ProcessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "job:";
    private static final Duration JOB_TTL = Duration.ofHours(1);

    public void initJob(String jobId) {
        saveJob(jobId, "PROCESSING", null, null);
    }

    public void completeJob(String jobId, ProcessResponse result) {
        saveJob(jobId, "DONE", result, null);
    }

    public void failJob(String jobId, String error) {
        saveJob(jobId, "FAILED", null, error);
    }

    private void saveJob(String jobId, String status, ProcessResponse result, String error) {
        try {
            String key = KEY_PREFIX + jobId;

            JobStatusEvent jobStatus = JobStatusEvent.builder()
                    .status(status)
                    .result(result)
                    .error(error)
                    .build();

            String json = objectMapper.writeValueAsString(jobStatus);

            redisTemplate.opsForValue().set(key, json, JOB_TTL);

        } catch (Exception e) {
            log.error("Error saving Redis Job {}: {}", jobId, e.getMessage());
        }
    }

    public JobStatusEvent getJobStatus(String jobId) {
        String key = KEY_PREFIX + jobId;
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return JobStatusEvent.builder().status("NOT_FOUND").build();
        }

        try {
            return objectMapper.readValue(json, JobStatusEvent.class);
        } catch (Exception e) {
            log.error("Lỗi parse JSON job {}: {}", jobId, e.getMessage());
            return JobStatusEvent.builder().status("ERROR_PARSING").build();
        }
    }
}

