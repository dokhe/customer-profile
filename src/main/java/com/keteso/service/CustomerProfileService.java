package com.keteso.service;


import com.keteso.entity.CustomerProfile;
import com.keteso.model.CustomerProfileRequest;
import com.keteso.model.request.*;
import com.keteso.responses.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
@Service
public interface CustomerProfileService {
    ResponseDTO customerInternalCheck(CustomerProfileRequest customerProfileRequest);

    ResponseDTO customerIprsCheck(CustomerProfileRequest customerProfileRequest);

    CustomerProfile createCustomerProfile(CustomerProfileRequest request);

    ResponseEntity<?> createOTP(CreateOTPReq request);

    ResponseEntity<?> validateOTP(ValidateOTPReq request) throws Exception;


    ResponseDTO handleCustomerRegistration(CustomerProfileRequest customerProfileRequest);

    ResponseEntity<?> createPIN(CreatePINReq request) throws Exception;

//    ResponseEntity<?> resetPIN(CreatePINReq request) throws Exception;

    ResponseEntity<?> validatePIN(ValidatePINReq request) throws Exception;

    ResponseEntity<?> validateToken(ValidateTokenReq request);

//    ResponseDTO fetchCustomerDetails(CustomerProfileRequest customerProfileRequest);

    ResponseDTO fetchCustomerDetails(String nationalId);

    String encrypt(String request) throws Exception;
}

