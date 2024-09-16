package com.keteso.utils;


import com.google.gson.Gson;
import com.keteso.model.CustomRequestHeader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@NoArgsConstructor
public class Utils {

//    private final Validator validator;
    @Value("${service.payload.salt}")
     String salt;

    @Value("${service.api-enable-key-sign}")
    boolean enableKeySign;

    Gson gson = new Gson();


    public String generateToken(String fieldsToHash) {
        String generatedHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(fieldsToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedHash = sb.toString();
        } catch (Exception ex) {
            log.info("Error generating SHA-512 file hash: " + ex.getMessage());
        }

        log.info("REQUEST SIGNATURE :: " + generatedHash);
        return generatedHash;
    }

    public boolean isKeySignatureValid(String keySignature, String requestIdTimestamp) {

        log.info("enableKeySign " + enableKeySign);
        if(!enableKeySign){
            return true;
        }
        if(keySignature == null){
            return false;
        }

        if(requestIdTimestamp == null){
            return false;
        }

        return keySignature.equals(generateToken(requestIdTimestamp));
    }

    public Date addMinutesToDate(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);

        return calendar.getTime();
    }

    public String generateRandomOtp() {
        Random rand = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int random = rand.nextInt(10);
            stringBuilder.append(random);
        }
        return stringBuilder.toString();
    }

    public String getFormattedDate(Date retryAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(retryAt);
    }


    public String genConversationId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String genTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return sdf.format(date);
    }

    public String sanitizePhoneNo(String primaryPhone) {
        if (!primaryPhone.startsWith("254")) {
            if (primaryPhone.startsWith("0")) {
                StringBuilder sb = new StringBuilder(primaryPhone);
                sb.deleteCharAt(0);
                primaryPhone = "254" + sb;
            } else {
                primaryPhone = "254" + primaryPhone;
            }
        }

        return primaryPhone;
    }

    public boolean isNotNull(String dataItem) {

        return (dataItem != null && dataItem.length() > 0);
    }

    public String getUniqueId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public String generateBasicAuthToken(String username, String password){
        String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + encoding;
    }

    public void logObject(String message, Object object){
        log.info("{} {}", message, gson.toJson(object));
    }
    // New method to generate messageID
    public String generateMessageId() {
        return UUID.randomUUID().toString();
    }
    public CustomRequestHeader validateAndGetHeaders(HttpServletRequest servletRequest) throws MethodArgumentNotValidException {
        CustomRequestHeader customRequestHeader = new CustomRequestHeader();
        customRequestHeader.setFeatureCode(servletRequest.getHeader("X-FeatureCode"));
        customRequestHeader.setFeatureName(servletRequest.getHeader("X-FeatureName"));
        customRequestHeader.setServiceCode(servletRequest.getHeader("X-ServiceCode"));
        customRequestHeader.setServiceName(servletRequest.getHeader("X-ServiceName"));
        customRequestHeader.setServiceSubCategory(servletRequest.getHeader("X-ServiceSubCategory"));
        customRequestHeader.setMinorServiceVersion(servletRequest.getHeader("X-MinorServiceVersion"));
        customRequestHeader.setChannelCategory(servletRequest.getHeader("X-ChannelCategory"));
        customRequestHeader.setChannelCode(servletRequest.getHeader("X-ChannelCode"));
        customRequestHeader.setChannelName(servletRequest.getHeader("X-ChannelName"));
        customRequestHeader.setRouteCode(servletRequest.getHeader("X-RouteCode"));
        customRequestHeader.setTimestamp(servletRequest.getHeader("X-Timestamp"));
        customRequestHeader.setServiceMode(servletRequest.getHeader("X-ServiceMode"));
        customRequestHeader.setSubscriberEvents(servletRequest.getHeader("X-SubscriberEvents"));
        customRequestHeader.setCallbackURL(servletRequest.getHeader("X-CallbackURL"));

//        // Validate the object
//        Set<ConstraintViolation<CustomRequestHeader>> violations = validator.validate(customRequestHeader);
//        if (!violations.isEmpty()) {
//            throw new MethodArgumentNotValidException(null, createMethodArgumentNotValidException(violations));
//        }

        return customRequestHeader;
    }
    public BindingResult createMethodArgumentNotValidException(
            Set<ConstraintViolation<CustomRequestHeader>> violations) {

        // Create a BindingResult from the violations
        BindingResult bindingResult = new org.springframework.validation.BeanPropertyBindingResult(
                new Object(), "object");

        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            bindingResult.addError(new FieldError(
                    "object", fieldName, errorMessage));
        }

        return bindingResult;
    }
}
