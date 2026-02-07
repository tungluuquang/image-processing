package com.ip.processservice.controller;

import com.ip.processservice.dto.request.ProcessRequest;
import com.ip.processservice.dto.response.ProcessResponse;
import com.ip.processservice.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/process")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;

    @PostMapping
    public ResponseEntity<ProcessResponse> processImage(@RequestBody ProcessRequest request) {
        ProcessResponse response = processService.execute(request);
        return ResponseEntity.ok(response);
    }


}
