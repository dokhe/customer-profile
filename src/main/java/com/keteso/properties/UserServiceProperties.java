package com.keteso.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = "service")
public class UserServiceProperties
{
    private String apiUsername;
    private String apiPassword;
}
