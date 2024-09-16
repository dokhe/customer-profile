package com.keteso.exceptions;

import com.keteso.properties.APIStatusProperties;
import com.keteso.responses.ResponseDTO;
import com.keteso.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import javax.crypto.BadPaddingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@ControllerAdvice
@Slf4j
@AllArgsConstructor
public class ControllerAdvise {
    private final APIStatusProperties apiStatusProperties;
    private final Utils utils;

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.error("===============<MethodArgumentNotValidException Error> Message: " + ex.getMessage());

        ResponseDTO response = new ResponseDTO();
        response.setStatus(apiStatusProperties.getApiErrorCode());
        response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setStatusMessage("Argument Validation Error");

        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors())
        {
            details.add(error.getDefaultMessage());
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("errors", details);

        response.setAdditionalData(map);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RequestValidationException.class)
    public ResponseEntity<?> validationExceptionHandler(RequestValidationException validationException) {
        log.error("===============<Validation Error> Message: " + validationException.getMessage());

        ResponseDTO response = new ResponseDTO();
        response.setStatus(apiStatusProperties.getApiErrorCode());
        response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setStatusMessage(validationException.getMessage());

        List<String> details = new ArrayList<>();
        details.add(validationException.getMessage());

        HashMap<String, Object> map = new HashMap<>();
        map.put("errors", details);

        response.setAdditionalData(map);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BadPaddingException.class)
    public ResponseEntity<?> badPaddingExceptionHandler(BadPaddingException validationException) {
        log.error("===============<BadPadding Error> Message: " + validationException.getMessage());

        ResponseDTO response = new ResponseDTO();
        response.setStatus(apiStatusProperties.getApiErrorCode());
        response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setStatusMessage(validationException.getMessage());

        List<String> details = new ArrayList<>();
        details.add(validationException.getMessage());

        HashMap<String, Object> map = new HashMap<>();
        map.put("errors", details);

        response.setAdditionalData(map);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<?> SQLExceptionHandler(SQLException validationException) {
        log.error("===============<SQLException Error> Message: " + validationException.getMessage());

        ResponseDTO response = new ResponseDTO();
        response.setStatus(apiStatusProperties.getApiErrorCode());
        response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setStatusMessage("An Error Occurred. Please Contact Support Team");

        List<String> details = new ArrayList<>();
        details.add("An Error Occurred. Please Contact Support Team");

        HashMap<String, Object> map = new HashMap<>();
        map.put("errors", details);

        response.setAdditionalData(map);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<?> NoResourceFoundExceptionHandler(NoResourceFoundException validationException) {
        log.error("===============<NoResourceFoundException Error> Message: " + validationException.getMessage());

        validationException.printStackTrace();

        ResponseDTO response = new ResponseDTO();
        response.setStatus(apiStatusProperties.getApiErrorCode());
        response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setStatusMessage(validationException.getLocalizedMessage());

        List<String> details = new ArrayList<>();
        details.add(validationException.getLocalizedMessage());

        HashMap<String, Object> map = new HashMap<>();
        map.put("errors", details);

        response.setAdditionalData(map);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> ExceptionHandler(Exception validationException) {
        log.error("===============<Exception Error> Message: " + validationException.getMessage());

        validationException.printStackTrace();

        ResponseDTO response = new ResponseDTO();
        response.setStatus(apiStatusProperties.getApiErrorCode());
        response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setStatusMessage("An Error Occurred. Please Contact Support Team");

        List<String> details = new ArrayList<>();
        details.add("An Error Occurred. Please Contact Support Team");

        HashMap<String, Object> map = new HashMap<>();
        map.put("errors", details);

        response.setAdditionalData(map);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}