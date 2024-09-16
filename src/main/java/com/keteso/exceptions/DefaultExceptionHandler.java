package com.keteso.exceptions;


import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @SneakyThrows
//    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NotNull HttpHeaders headers,
                                                                  @NotNull HttpStatusCode status,
                                                                  @NotNull WebRequest request) {

        StringBuilder msg = new StringBuilder();
        List<Object> errors = new ArrayList<>();
        var errorValues = ex.getBindingResult().getFieldErrors();
        for (FieldError errorValue : errorValues) {
            errors.add(errorValue.getDefaultMessage());
            msg.append(errorValue.getDefaultMessage()).append(",");
        }

        log.info("messages " + msg.toString());

        return null;
    }

//    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        log.error("Error occurred: {}", ex.getHttpInputMessage());
        List<Object> error = new ArrayList<>();
        error.add(ex.getMessage());

        return null;
    }
}
