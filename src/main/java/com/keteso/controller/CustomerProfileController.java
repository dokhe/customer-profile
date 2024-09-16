package com.keteso.controller;

import com.keteso.model.CustomerProfileRequest;
import com.keteso.model.request.*;
import com.keteso.properties.APIStatusProperties;
import com.keteso.responses.ResponseDTO;
import com.keteso.service.CustomerProfileService;
import com.keteso.utils.Utils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dokhe
 */
@RestController
@RequestMapping("/api/v1/customer")
@Slf4j
public class CustomerProfileController {
    @Autowired
    CustomerProfileService customerProfileService;
    @Autowired
    APIStatusProperties apiStatusProperties;
    @Autowired
    Utils utils;
    @PostMapping("/register")
    public ResponseDTO registerCustomer(@RequestBody CustomerProfileRequest customerProfileRequest) {
        ResponseDTO response = customerProfileService.handleCustomerRegistration(customerProfileRequest);
        return response;
    }
    @PostMapping("/internal-check")
    public ResponseDTO checkCustomerInternal(@RequestBody CustomerProfileRequest customerProfileRequest) {
        return customerProfileService.customerInternalCheck(customerProfileRequest);
    }
    @PostMapping("/iprs-check")
    public ResponseDTO checkCustomerIprs(@RequestBody CustomerProfileRequest customerProfileRequest) {
        return customerProfileService.customerIprsCheck(customerProfileRequest);
    }
    @PostMapping("/create")
    public ResponseEntity<String> createCustomer(@RequestBody CustomerProfileRequest customerProfileRequest) {
        try {
            String result = String.valueOf(customerProfileService.createCustomerProfile(customerProfileRequest));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create customer profile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping(path = "/create-otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOTP(@Valid @RequestBody CreateOTPReq request) {

        return customerProfileService.createOTP(request);
    }

    @PostMapping(path = "/validate-otp", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateOTP(@Valid @RequestBody ValidateOTPReq request) throws Exception {

        return customerProfileService.validateOTP(request);
    }

    @PostMapping(path = "/create-pin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPIN(@Valid @RequestBody CreatePINReq request) throws Exception {

        return customerProfileService.createPIN(request);
    }

//    @PostMapping(path = "/reset-pin/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> resetPIN(@Valid @RequestBody CreatePINReq request,
//                                      ) throws Exception {
//
//        return authService.resetPIN(request);
//    }

    @PostMapping(path = "/validate-pin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validatePIN(@Valid @RequestBody ValidatePINReq request) throws Exception {

        return customerProfileService.validatePIN(request);
    }

    @PostMapping(path = "/validate-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateToken(@Valid @RequestBody ValidateTokenReq request) {


        return customerProfileService.validateToken(request);
    }

    @PostMapping("/test/encrypt")
    public String testEncrypt(@RequestBody @Valid String request) throws Exception {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("INCOMING REQUEST : " + request);
        log.info("-----------------------------------------------------------------------------------------");

        return customerProfileService.encrypt(request);
    }
    @GetMapping("/fetchCustomerDetails/{nationalId}")
    public ResponseEntity<ResponseDTO> fetchCustomerDetails(@PathVariable String nationalId) {
        ResponseDTO response = customerProfileService.fetchCustomerDetails(nationalId);
        return ResponseEntity.ok(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleException(Exception e) {
        ResponseDTO response = new ResponseDTO();
        response.setStatus(apiStatusProperties.getApiErrorCode());
        response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
        response.setStatusMessage("An unexpected error occurred: " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}