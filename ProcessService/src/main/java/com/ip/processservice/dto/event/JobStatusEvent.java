package com.ip.processservice.dto.event;

import com.ip.processservice.dto.response.ProcessResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobStatusEvent {
    private String status;
    private ProcessResponse result;
    private String error;
}
