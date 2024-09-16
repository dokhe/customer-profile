package com.keteso.model.outgoingRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class IprsRequest {
    private Header header;
    private IprsRequestPayload requestPayload;

    @Data
    public static class Header {
        @JsonProperty("messageID")
        private String messageID;

        @JsonProperty("featureCode")
        private String featureCode;

        @JsonProperty("featureName")
        private String featureName;

        @JsonProperty("serviceCode")
        private String serviceCode;

        @JsonProperty("serviceName")
        private String serviceName;

        @JsonProperty("serviceSubCategory")
        private String serviceSubCategory;

        @JsonProperty("minorServiceVersion")
        private String minorServiceVersion;

        @JsonProperty("channelCode")
        private String channelCode;

        @JsonProperty("channelName")
        private String channelName;

        @JsonProperty("routeCode")
        private String routeCode;

        @JsonProperty("timeStamp")
        private String timeStamp;

        @JsonProperty("serviceMode")
        private String serviceMode;

        @JsonProperty("subscribeEvents")
        private String subscribeEvents;

        @JsonProperty("callBackURL")
        private String callBackURL;
    }

    @Data
    public static class IprsRequestPayload {
        private PrimaryData primaryData;
        private List<AdditionalData> additionalData; // Changed to List

        @Data
        public static class PrimaryData {
            @JsonProperty("businessKey")
            private String businessKey;

            @JsonProperty("businessKeyType")
            private String businessKeyType;
        }

        @Data
        public static class AdditionalData {
            @JsonProperty("companyCode")
            private String companyCode;
        }
    }
}

