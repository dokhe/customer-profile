package com.keteso.utils;

import com.keteso.model.AdditionalData;
import com.keteso.model.CustomerProfileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class RequestValidation {

    public HashMap<String, String> validateRequest(CustomerProfileRequest requestWrapper){

        HashMap<String,String> objectHashMap = new HashMap<>();

        boolean validation = true;
        String failureReason = "";

        if (checkIfNullString(String.valueOf(requestWrapper.getPrimaryData().getDocuments().getDocumentType())).equals(true)) {
            validation = false;
            failureReason = "Document type validation error";
        }

        if (checkIfNullString(requestWrapper.getPrimaryData().getDocuments().getDocumentNumber()).equals(true)) {
            validation = false;
            failureReason = "Document Number validation error";
        }
        if (checkIfNullString(requestWrapper.getPrimaryData().getMobileNumber()).equals(true)) {
            validation = false;
            failureReason = "Mobile Number validation error";
        }
        if (checkIfNullString(String.valueOf(requestWrapper.getPrimaryData()
                .getDateOfBirth())).equals(true)) {
            validation = false;
            failureReason = "Date of birth validation error";
        }
        if (checkIfNullString(requestWrapper.getPrimaryData().getFirstName()).equals(true)) {
            validation = false;
            failureReason = "Full Name validation error";
        }
        if (checkIfNullString(requestWrapper.getPrimaryData().getNationalId()).equals(true)) {
            validation = false;
            failureReason = "ID Number validation error";
        }

        List<AdditionalData> additionalDataList = requestWrapper.getAdditionalData();
        if (additionalDataList != null) {
            for (AdditionalData additionalData : additionalDataList) {

                if (checkIfNullString(additionalData.getKey()).equals(true)){
                    validation = false;
                    failureReason = "Additional Data Key validation error";
                }else if (checkIfNullString(additionalData.getValue()).equals(true)){
                    validation = false;
                    failureReason = "Additional Data Value  validation error";
                }

            }
        }

        objectHashMap.put("validation", String.valueOf(validation));
        objectHashMap.put("failureReason", failureReason);
        return objectHashMap;
    }
    // Validate request headers and return a map with validation status and failure reasons
    public HashMap<String, String> validateRequestHeaders(
            String featureCode, String featureName, String serviceCode, String serviceName,
            String serviceSubCategory, String minorServiceVersion, String channelCategory,
            String channelCode, String channelName, String routeCode, String timestamp,
            String serviceMode, String subscriberEvents, String callbackURL) {

        HashMap<String, String> validationResults = new HashMap<>();
        boolean isValid = true;
        StringBuilder failureReasons = new StringBuilder();

        // Perform validation
        if (checkIfNullString(featureName)) {
            isValid = false;
            failureReasons.append("Feature Name is required. ");
        }
        if (checkIfNullString(serviceCode)) {
            isValid = false;
            failureReasons.append("Service Code is required. ");
        }
        if (checkIfNullString(serviceName)) {
            isValid = false;
            failureReasons.append("Service Name is required. ");
        }
        if (checkIfNullString(serviceSubCategory)) {
            isValid = false;
            failureReasons.append("Service sub category is required. ");
        }
        if (checkIfNullString(channelCode)) {
            isValid = false;
            failureReasons.append("Channel code is required. ");
        }
        if (checkIfNullString(channelName)) {
            isValid = false;
            failureReasons.append("Channel Name is required. ");
        }
        if (checkIfNullString(timestamp)) {
            isValid = false;
            failureReasons.append("Timestamp is required. ");
        }

        validationResults.put("validation", String.valueOf(isValid));
        validationResults.put("failureReason", failureReasons.toString());
        return validationResults;
    }
    public Boolean checkIfNullString(String string){

        return string == null || string.isEmpty();
    }
}
