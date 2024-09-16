package com.keteso.responses;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
public class ResponseDTO {
    private String requestId;
    private String conversationId;
    private String channel;
    private String timestamp;
    private String status;
    private String statusDesc;
    private String statusMessage;
    private Object additionalData;
    private HttpStatusCode statusCode; // Add this field to store HttpStatusCode

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
