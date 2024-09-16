package com.keteso.exceptions;

public class RequestValidationException extends RuntimeException{
    String requestId;
    String conversationId;
    String timeStamp;
    public RequestValidationException(String message) {
        super(message);
    }

    public RequestValidationException(String requestId, String conversationId, String timeStamp, String message) {
        super(message);
        this.requestId = requestId;
        this.conversationId = conversationId;
        this.timeStamp = timeStamp;
    }
}
