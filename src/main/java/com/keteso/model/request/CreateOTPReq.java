package com.keteso.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateOTPReq extends Identifier {
    @NotNull(message = "additionalData is required")
    @Valid
    private AdditionalData additionalData;
    @Data
    public static class AdditionalData {
        @NotBlank(message = "identifier is required")
        private String identifier;
        @NotBlank(message = "customerName is required")
        private String customerName;
        private String sms;
    }
}

