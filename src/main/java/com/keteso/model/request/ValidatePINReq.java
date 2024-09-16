package com.keteso.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ValidatePINReq extends Identifier {
    @NotNull(message = "additionalData is required")
    @Valid
    private AdditionalData additionalData;
    @Data
    public static class AdditionalData {
        @NotBlank(message = "authType is required")
        private String authType;
        @NotBlank(message = "identifier is required")
        private String identifier;
        @NotBlank(message = "pin is required")
        private String pin;
//        @NotBlank(message = "imsi is required")
//        private String imsi;
    }
}
