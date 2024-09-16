package com.keteso.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = "service")
public class APIStatusProperties
{
    private String apiSuccessCode;
    private String apiSuccessCodeDesc;
    private String apiErrorCode;
    private String apiErrorCodeDesc;
    private String apiPinMaxAttemptsCode;
    private String apiPinMaxAttemptsCodeDesc;
}
