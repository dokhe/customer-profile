package com.keteso.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Identifier {
    @NotBlank(message = "requestId is required")
    private String requestId;
    @NotBlank(message = "channel is required")
    private String channel;
    @NotBlank(message = "timestamp is required")
    private String timestamp;
}
