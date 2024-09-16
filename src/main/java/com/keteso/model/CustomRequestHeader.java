package com.keteso.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomRequestHeader {
    private String featureCode;
    @NotBlank(message = "X-FeatureName is required")
    private String featureName;
    @NotBlank(message = "X-ServiceCode is required")
    private String serviceCode;
    @NotBlank(message = "X-ServiceName is required")
    private String serviceName;
    private String serviceSubCategory;
    private String minorServiceVersion;
    @NotBlank(message = "X-ChannelCategory is required")
    private String channelCategory;
    @NotBlank(message = "X-ChannelCode is required")
    private String channelCode;
    @NotBlank(message = "X-ChannelName is required")
    private String channelName;
    private String routeCode;
    private String timestamp;
    private String serviceMode;
    private String subscriberEvents;
    private String callbackURL;
    // private String authorization;
}

