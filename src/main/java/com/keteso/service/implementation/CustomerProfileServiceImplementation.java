package com.keteso.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keteso.entity.*;
import com.keteso.enums.Status;
import com.keteso.model.CustomerProfileRequest;
import com.keteso.model.request.*;
import com.keteso.properties.APIStatusProperties;
import com.keteso.properties.EntityStatusProperties;
import com.keteso.repository.*;
import com.keteso.responses.PinDTO;
import com.keteso.responses.ResponseDTO;
import com.keteso.responses.UserPrincipal;
import com.keteso.service.CustomerProfileService;
import com.keteso.utils.EncryptService;
import com.keteso.utils.RequestValidation;
import com.keteso.utils.TokenProvider;
import com.keteso.utils.Utils;
import com.keteso.webClient.WebClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class CustomerProfileServiceImplementation implements CustomerProfileService {
    private final Gson gson;
    @Autowired
    Utils utils;
    @Autowired
    WebClientImpl webClient;
    @Autowired
    APIStatusProperties apiStatusProperties;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    ChannelEntityRepo channelRepo;
    @Autowired
    OtpEntityRepo otpRepo;
    @Autowired
    OtpHistEntityRepo otpHistRepo;
    @Autowired
    PinEntityRepo pinRepo;
    @Autowired
    PinHistEntityRepo pinHistRepo;
    @Autowired
    EncryptService encryptService;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    TokenEntityRepo tokenEntityRepo;
    @Value("${req.hd.channel}")
    String channel;
    @Value("${req.hd.createdBy}")
    String createdBy;
    @Value("${req.hd.requestId}")
    String requestId;
    @Value("${service.api-pin-salt}")
    String secret;
    @Value("${service.api-enable-test-encrypt}")
    boolean enableTestEncrypt;
    @Autowired
    SlnDocumentsRepository slnDocumentsRepo;
    @Autowired
    SlnGenderRepository slnGenderRepository;
    //    private String secret = "$2a$10$gXxDkFW44WT5n0Jr8LQyZOCHGLRleM6X62YJgmocqIqxa5/ycLYiO";
    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    EntityStatusProperties entityStatusProperties;
    @Autowired
    RequestValidation requestValidation; // Inject RequestValidation

    public CustomerProfileServiceImplementation(Gson gson) {
        this.gson = gson;
    }

    public ResponseDTO handleCustomerRegistration(CustomerProfileRequest customerProfileRequest) {
        ResponseDTO response = new ResponseDTO();

        // Validate the request
        HashMap<String, String> validationResults = requestValidation.validateRequest(customerProfileRequest);
        boolean isValid = Boolean.parseBoolean(validationResults.get("validation"));
        String failureReason = validationResults.get("failureReason");

        if (!isValid) {
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Request validation failed: " + failureReason);
            return response;
        }

        try {
            // Perform internal customer check
            ResponseDTO internalCheckResponse = customerInternalCheck(customerProfileRequest);
            HttpStatusCode internalCheckStatus = internalCheckResponse.getStatusCode();

            // Perform IPRS check
            ResponseDTO iprsCheckResponse = customerIprsCheck(customerProfileRequest);
            HttpStatusCode iprsCheckStatus = iprsCheckResponse.getStatusCode();

            // Create the customer profile regardless of IPRS check status
            CustomerProfile customerProfile = createCustomerProfile(customerProfileRequest);

            // Check if IPRS check returned 200 and internal check did not
            if (iprsCheckStatus != null && iprsCheckStatus.is2xxSuccessful() &&
                    (internalCheckStatus == null || !internalCheckStatus.is2xxSuccessful())) {

                // Create OTP for the customer
                CreateOTPReq otpReq = new CreateOTPReq();
                CreateOTPReq.AdditionalData additionalData = new CreateOTPReq.AdditionalData();
                additionalData.setIdentifier(customerProfile.getMobileNumber());
                additionalData.setCustomerName(customerProfile.getFirstName());

                otpReq.setAdditionalData(additionalData);
                otpReq.setChannel(customerProfileRequest.getPrimaryData().getChannelCode());
                otpReq.setRequestId(UUID.randomUUID().toString()); // Generate a unique request ID
                otpReq.setTimestamp(LocalDateTime.now().toString()); // Use current timestamp
                ResponseEntity<?> otpResponse = createOTP(otpReq);

                response.setStatus(apiStatusProperties.getApiSuccessCode());
                response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());
                response.setStatusMessage("Customer profile created successfully.");
            } else {
                // Either the internal check was successful or IPRS check was not successful
                response.setStatus(apiStatusProperties.getApiErrorCode());
                response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
                response.setStatusMessage("The customer with " + customerProfileRequest.getPrimaryData().getNationalId() + " exists in the system or IPRS check failed.");
            }
        } catch (Exception e) {
            log.error("Error handling customer registration: ", e);
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Customer profile with the same identification type and number already exists");
        }
        return response;
    }
    public ResponseDTO customerInternalCheck(CustomerProfileRequest customerProfileRequest) {
        ResponseDTO response = new ResponseDTO();
        try {
            ResponseEntity<?> internalCheckResponse = webClient.customerInternalCheck(customerProfileRequest);
            HttpStatusCode httpStatusCode = internalCheckResponse.getStatusCode();
            log.info("Internal Check HTTP Status Code: {}", httpStatusCode);

            response.setStatusCode(httpStatusCode); // Set HttpStatusCode directly
            if (httpStatusCode.is2xxSuccessful()) {
                response.setStatus(apiStatusProperties.getApiSuccessCode());
                response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());
                response.setStatusMessage("Customer with " + customerProfileRequest.getPrimaryData().getNationalId() + " exists internally.");
            } else {
                response.setStatus(apiStatusProperties.getApiErrorCode());
                response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
                response.setStatusMessage("Internal check failed.");
            }
        } catch (Exception e) {
            log.error("Exception occurred during internal check: {}", e.getMessage());
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Exception occurred during internal check.");
        }
        return response;
    }
    public ResponseDTO customerIprsCheck(CustomerProfileRequest customerProfileRequest) {
        ResponseDTO response = new ResponseDTO();
        try {
            ResponseEntity<?> iprsCheckResponse = webClient.customerIprsCheck(customerProfileRequest);
            HttpStatusCode httpStatusCode = iprsCheckResponse.getStatusCode();
            log.info("IPRS Check HTTP Status Code: {}", httpStatusCode);

            response.setStatusCode(httpStatusCode); // Set HttpStatusCode directly
            if (httpStatusCode.is2xxSuccessful()) {
                response.setStatus(apiStatusProperties.getApiSuccessCode());
                response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());
                response.setStatusMessage("Customer with " + customerProfileRequest.getPrimaryData().getNationalId() + " exists in IPRS.");
            } else {
                response.setStatus(apiStatusProperties.getApiErrorCode());
                response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
                response.setStatusMessage("IPRS check failed.");
            }
        } catch (Exception e) {
            log.error("Exception occurred during IPRS check: {}", e.getMessage());
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Exception occurred during IPRS check.");
        }
        return response;
    }
    public CustomerProfile createCustomerProfile(CustomerProfileRequest request) {
        CustomerProfile customerProfile = new CustomerProfile();

        /* Fetch the SlnDocuments entity based on the documentType */
        SlnDocuments document = slnDocumentsRepo.findById(request.getPrimaryData().getDocuments().getDocumentType())
                .orElseThrow(() -> new RuntimeException("Document type not found"));

        /* Check for existing customer profile with the same ID type and number */
        if (customerRepo.existsByIdentificationTypeAndNationalId(document, request.getPrimaryData().getNationalId())) {
            throw new RuntimeException("Customer profile with the same identification type and number already exists");
        }

        customerProfile.setNationalId(request.getPrimaryData().getNationalId());
        customerProfile.setCustomerIdentifier(UUID.randomUUID());
        customerProfile.setIdentificationType(document);
        customerProfile.setDocumentNumber(request.getPrimaryData().getDocuments().getDocumentNumber());
        customerProfile.setMobileNumber(request.getPrimaryData().getMobileNumber());
        customerProfile.setDateOfBirth(request.getPrimaryData().getDateOfBirth());
        customerProfile.setFirstName(request.getPrimaryData().getFirstName());
        customerProfile.setMiddleName(request.getPrimaryData().getMiddleName());
        customerProfile.setLastName(request.getPrimaryData().getLastName());
        customerProfile.setCreatedAt(Timestamp.valueOf(LocalDateTime.now(ZoneId.systemDefault())));
        customerProfile.setCreatedBy(createdBy);

        /* Fetch the SlnGender entity based on a valid ID */
        SlnGender gender = slnGenderRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Gender not found"));
        customerProfile.setSlnGender(gender);

        /* Ensure that status is correctly assigned */
        Integer statusValue = Status.INACTIVE.getValue(); /* Ensure this returns Integer */
        customerProfile.setStatus(statusValue);

        try {
            customerProfile = customerRepo.save(customerProfile);
            log.info("CustomerProfile saved: " + customerProfile);

            /* Create OTP */
            CreateOTPReq otpReq = new CreateOTPReq();
            CreateOTPReq.AdditionalData additionalData = new CreateOTPReq.AdditionalData();
            additionalData.setIdentifier(customerProfile.getMobileNumber());
            additionalData.setCustomerName(customerProfile.getFirstName());

            otpReq.setAdditionalData(additionalData);
            otpReq.setChannel(request.getPrimaryData().getChannelCode()); // Set the channel from the request
            otpReq.setRequestId(request.getPrimaryData().getRequestId()); // Set the requestId from the request
            otpReq.setTimestamp(LocalDateTime.now().toString()); // Use current timestamp

            ResponseEntity<?> otpResponse = createOTP(otpReq);
            log.info("OTP Response: " + otpResponse.getBody());

            return customerProfile;
        } catch (DataIntegrityViolationException e) {
            log.error("Constraint violation occurred: " + e.getMessage());
            throw new RuntimeException("Failed to save customer profile due to a constraint violation");
        } catch (Exception e) {
            log.error("Error saving customer profile: ", e);
            throw new RuntimeException("Failed to save customer profile");
        }
    }
    public ResponseEntity<?> createOTP(CreateOTPReq request) {

        ResponseDTO response = new ResponseDTO();
        response.setTimestamp(request.getTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setRequestId(request.getRequestId());
        response.setChannel(request.getChannel());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();
        log.info("Request = " + gson.toJson(request));
        Optional<ChannelEntity> channelOpt = channelRepo.findByChannelCode(request.getChannel());
        if (channelOpt.isEmpty()) {
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Channel does not exist.");

            utils.logObject("Response", response);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        ChannelEntity channel = channelOpt.get();

        //Check if OTP exists

        CreateOTPReq.AdditionalData additionalData = request.getAdditionalData();

        String identifier = additionalData.getIdentifier();

        OtpEntity otp = new OtpEntity();
        Optional<OtpEntity> otpOpt = otpRepo.findByChannelIdAndIdentifier(channel.getRecNo(), identifier);
        if (otpOpt.isPresent()) {
            otp = otpOpt.get();
        }
        otp.setChannelId(channel.getRecNo());
        otp.setIdentifier(identifier);
        String rawOtp = utils.generateRandomOtp();
        log.info("OTP ===> " + rawOtp);
        String complexOtp = rawOtp + identifier + secret;
        String hashedOtp = bCryptPasswordEncoder.encode(complexOtp);
        otp.setOtp(hashedOtp);
        Date date = new Date();
        Date expiryDate = utils.addMinutesToDate(date, channel.getOtpMaxLife());
        otp.setExpiryDateTime(expiryDate);
        otp.setStatus(1);
        otp.setCreatedAt(date);
        otp.setCreatedBy("API");
        otp.setAttempts(0);
        otp.setRetryAt(date);

        otpRepo.save(otp);

        if (otpOpt.isPresent()) {
            OtpEntity otpOld = otpOpt.get();
            //Move this to OTP History
            log.info("Move OTP to History");
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                OtpHistEntity hist = objectMapper.readValue(Objects.requireNonNull(objectMapper.writeValueAsString(otpOld)), OtpHistEntity.class);
                hist.setRecNo(null);
                hist.setOtpId(otpOld.getRecNo());
                hist.setCreatedAt(new Date());
                hist.setCreatedBy("API");

                otpHistRepo.save(hist);
            } catch (Exception e) {
                log.error("An Error " + e.getLocalizedMessage());
            }
        }

        String sms = request.getAdditionalData().getSms();
        if (utils.isNotNull(sms)) {
//            sms = sms.replace("{retailer}", request.getAdditionalData().getCustomerName());
            sms = sms.replace("{customer}", request.getAdditionalData().getCustomerName());
            sms = sms.replace("{OTP}", rawOtp);

            sms = sms.replaceAll("\\{.*\\}", "");

            webClient.scheduleSendSms(identifier, sms);
        }
        response.setStatus(apiStatusProperties.getApiSuccessCode());
        response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());
        response.setStatusMessage("OTP generated successfully");

        utils.logObject("Successful response", response);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
    public ResponseEntity<?> validateOTP(ValidateOTPReq request) throws Exception {
        Date dateNow = new Date();
        ResponseDTO response = new ResponseDTO();
        response.setTimestamp(request.getTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setRequestId(request.getRequestId());
        response.setChannel(request.getChannel());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();
        String additionalData = gson.toJson(request.getAdditionalData());
        HashMap<String, String> map = gson.fromJson(additionalData, HashMap.class);

        String userOtp = map.getOrDefault("otp", "");

        String identifier = map.getOrDefault("identifier", "");

        log.info("Request = " + gson.toJson(request));

        Optional<ChannelEntity> channelOpt = channelRepo.findByChannelCode(request.getChannel());
        if (channelOpt.isEmpty()) {
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Channel does not exist.");

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        ChannelEntity channel = channelOpt.get();

        Optional<OtpEntity> optOpt = otpRepo.findByChannelIdAndIdentifierAndStatus(channel.getRecNo(), identifier, entityStatusProperties.getActive());
        if (optOpt.isEmpty()) {
            log.info("OTP record not found.");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("OTP record not found.");

            utils.logObject("Response", response);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }
        OtpEntity otp = optOpt.get();
        //Check if OTP is expired
        if (dateNow.after(otp.getExpiryDateTime())) {
            log.info("Your OTP has expired");
            //Generate another OTP
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Your OTP has expired.");

            utils.logObject("Response", response);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }
        //Check attempts
        if (otp.getAttempts() + 1 >= channel.getPinMaxAttempts()) {
//            otp.setAttempts(0);
//            otpRepo.save(otp);

            log.info("You have entered OTP many times.");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("You have entered OTP many times.");

            utils.logObject("Response", response);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }
        //Check date now is after retryAt
        if (dateNow.before(otp.getRetryAt())) {
            log.info("You are locked until ...");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("You are locked until " + utils.getFormattedDate(otp.getRetryAt()) + ".");

            utils.logObject("Response", response);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        userOtp = encryptService.decrypt(userOtp);

        log.info("userOtp {}", userOtp);

        String complexOtp = userOtp + identifier + secret;
        boolean matches = bCryptPasswordEncoder.matches(complexOtp, otp.getOtp());
        if (!matches) {
            log.info("Invalid OTP.");
            otp.setAttempts(otp.getAttempts() + 1);
            if (otp.getAttempts() >= channel.getPinMaxAttempts()) {
                otp.setRetryAt(utils.addMinutesToDate(new Date(), channel.getPinRetryDelay()));
            }
            otpRepo.save(otp);

            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Invalid OTP");

            utils.logObject("Response", response);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } else {
            //Reset pin attempts to zero if pin record found
            Optional<PinEntity> pinOpt = pinRepo.findByChannelIdAndIdentifier(channel.getRecNo(), identifier);
            if (pinOpt.isPresent()) {
                PinEntity pin = pinOpt.get();
                pin.setAttempts(0);
                pinRepo.save(pin);
            }
            log.info("OTP validated successfully.");
            otp.setAttempts(0);
            otp.setStatus(entityStatusProperties.getDelete());
            otpRepo.save(otp);
            response.setStatus(apiStatusProperties.getApiSuccessCode());
            response.setStatusMessage("OTP validated successfully.");
            response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());

            utils.logObject("Response", response);
            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }
    }
    public ResponseEntity<?> createPIN(CreatePINReq request) throws Exception {

        Date dateNow = new Date();
        ResponseDTO response = new ResponseDTO();
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setRequestId(request.getRequestId());
        response.setChannel(request.getChannel());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();
        CreatePINReq.AdditionalData additionalData = request.getAdditionalData();
        String identifier = additionalData.getIdentifier();
        String userPin = additionalData.getPin();

        log.info("Request = " + gson.toJson(request));

        Optional<ChannelEntity> channelOpt = channelRepo.findByChannelCode(request.getChannel());
        if (channelOpt.isEmpty()) {
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Channel does not exist.");

            utils.logObject("Response", response);
            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        ChannelEntity channel = channelOpt.get();

        // Check if PIN exists
        Optional<PinEntity> pinOpt = pinRepo.findByChannelIdAndIdentifier(channel.getRecNo(), identifier);
        PinEntity pin = pinOpt.orElse(new PinEntity());  // Use new PinEntity if not present

        pin.setChannelId(channel.getRecNo());
        pin.setIdentifier(identifier);
        String rawPin = encryptService.decrypt(userPin);
        log.info("PIN ===> " + rawPin);
        String complexPin = rawPin + identifier + secret;
        String hashedPin = bCryptPasswordEncoder.encode(complexPin);
        pin.setPin(hashedPin);
        Date date = new Date();
        pin.setStatus(1);
        pin.setCreatedAt(date);
        pin.setCreatedBy("API");
        pin.setAttempts(0);
        pin.setRetryAt(date);

        pinRepo.save(pin);

        if (pinOpt.isPresent()) {
            PinEntity oldPin = pinOpt.get();
            // Move this to PIN History
            log.info("Move PIN to History");

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                PinHistEntity hist = objectMapper.readValue(objectMapper.writeValueAsString(oldPin), PinHistEntity.class);
                hist.setRecNo(null);
                hist.setPinId(oldPin.getRecNo());
                hist.setCreatedAt(new Date());
                hist.setCreatedBy("API");

                pinHistRepo.save(hist);

            } catch (Exception e) {
                log.error("Error " + e.getLocalizedMessage());
            }
        }
        response.setStatus(apiStatusProperties.getApiSuccessCode());
        response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());
        response.setStatusMessage("Password saved successfully.");

        utils.logObject("Response", response);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }


    public ResponseEntity<?> resetPIN(CreatePINReq request) throws Exception {
        log.info("Inside resetPIN");
        return createPIN(request);
    }
    public ResponseEntity<?> validatePIN(ValidatePINReq request) throws Exception {

        Date dateNow = new Date();
        ResponseDTO response = new ResponseDTO();
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setRequestId(request.getRequestId());
        response.setChannel(request.getChannel());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();
        ValidatePINReq.AdditionalData additionalData = request.getAdditionalData();
        String userPin = additionalData.getPin();
        log.info("Request = " + gson.toJson(request));

        Optional<ChannelEntity> channelOpt = channelRepo.findByChannelCode(request.getChannel());
        if (channelOpt.isEmpty()) {
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Channel does not exist.");

            utils.logObject("Response", response);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        ChannelEntity channel = channelOpt.get();

        //Check if we have PIN record
        Optional<PinEntity> pinOpt = pinRepo.findByChannelIdAndIdentifier(channel.getRecNo(), additionalData.getIdentifier());
        if (pinOpt.isPresent()) {
            //We have PIN record. So validate internally
            log.info("We have PIN record");
            ResponseDTO extResponse = validateInternalPIN(channel, request.getRequestId(), additionalData.getAuthType(), additionalData.getIdentifier(), encryptService.decrypt(additionalData.getPin()));

            utils.logObject("Response", extResponse);
            return new ResponseEntity<>(extResponse, headers, HttpStatus.OK);
        } else {
            //External OTP validate
            log.info("Pin Record Not Found.");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Identifier Not Found.");

            utils.logObject("Response", response);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }
    }

    private ResponseDTO validateInternalPIN(ChannelEntity channel, String requestId, String authType, String identifier, String userPin) {
        Date dateNow = new Date();
        ResponseDTO response = new ResponseDTO();
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setRequestId(requestId);
        response.setChannel(channel.getChannelCode());

        if (authType == null || (!authType.equals("Login") && !authType.equals("Transaction"))) {
            log.info("Auth is empty or invalid.");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Invalid auth type.");

            return response;
        }

        Optional<PinEntity> pinOpt = pinRepo.findByChannelIdAndIdentifier(channel.getRecNo(), identifier);
        if (pinOpt.isEmpty()) {
            log.info("PIN record not found.");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("PIN record not found.");

            return response;
        }

        PinEntity pin = pinOpt.get();

        //Check attempts
        if (pin.getAttempts() >= channel.getPinMaxAttempts()) {
            pin.setAttempts(0);
            pinRepo.save(pin);

            log.info("You have entered PIN many times.");
            response.setStatus(apiStatusProperties.getApiPinMaxAttemptsCode());
            response.setStatusDesc(apiStatusProperties.getApiPinMaxAttemptsCodeDesc());
            response.setStatusMessage("You have entered PIN many times.");

//            if(channel.getChannelCode().equalsIgnoreCase(api520Code)){
//                Optional<SubscriberSimEntity> simOpt = subscriberSimEntityRepo.findByMsisdn(identifier);
//                if(simOpt.isPresent()){
//                    SubscriberSimEntity simEntity = simOpt.get();
//                    simEntity.setStatus(entityStatusProperties.getSuspend());
//                    subscriberSimEntityRepo.save(simEntity);
//                }
//            }

            return response;
        }
        //Check date now is after retryAt
        if (dateNow.before(pin.getRetryAt())) {
            log.info("You are locked until ...");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("You are locked until " + utils.getFormattedDate(pin.getRetryAt()) + ".");

            return response;
        }


        String complexPin = userPin + identifier + secret;
        boolean matches = bCryptPasswordEncoder.matches(complexPin, pin.getPin());
        if (!matches) {
            log.info("Invalid PIN.");
            pin.setAttempts(pin.getAttempts() + 1);
            if (pin.getAttempts() >= channel.getPinMaxAttempts()) {
                pin.setRetryAt(utils.addMinutesToDate(new Date(), channel.getPinRetryDelay()));
            }
            pinRepo.save(pin);

            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Invalid PIN.");

            return response;
        } else {
            log.info("PIN validated successfully.");
            pin.setAttempts(0);
            response.setStatus(apiStatusProperties.getApiSuccessCode());
            response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());

            if (authType.equals("Login")) {

                HashMap<String, String> map = new HashMap<>();

                map.put("token", generateAccessToken(identifier));

                response.setAdditionalData(map);
            } else {
                response.setAdditionalData(null);
            }

            response.setStatusMessage("PIN validated successfully.");

            return response;
        }
    }
    public ResponseEntity<?> validateToken(ValidateTokenReq request) {
        Date dateNow = new Date();
        ResponseDTO response = new ResponseDTO();
        response.setTimestamp(utils.genTimestamp());
        response.setConversationId(utils.genConversationId());
        response.setRequestId(request.getRequestId());
        response.setChannel(request.getChannel());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Gson gson = new Gson();
        ValidateTokenReq.AdditionalData additionalData = request.getAdditionalData();

        String token = additionalData.getToken();

        utils.logObject("Request", request);

        if (!tokenProvider.isTokenValid("val", token)) {
            log.error("Token invalid");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Token is invalid.");

            utils.logObject("Request", request);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        Optional<TokenEntity> tokenEntityOpt = tokenEntityRepo.findByToken(token);
        if (tokenEntityOpt.isEmpty()) {
            log.error("Token not found");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Token Not Found.");

            utils.logObject("Request", request);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        TokenEntity tokenEntity = tokenEntityOpt.get();
        if (tokenEntity.getStatus() != 1) {
            log.error("Token status is not active");
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Token is invalid.");

            utils.logObject("Request", request);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        log.error("Token is okay");
        response.setStatus(apiStatusProperties.getApiSuccessCode());
        response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());
        response.setStatusMessage("Token is valid.");

        utils.logObject("Request", request);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
    @Transactional
    private String generateAccessToken(String identifier) {
        PinDTO pinDTO = new PinDTO();
        pinDTO.setIdentifier(identifier);
        String token = tokenProvider.createAccessToken(getUserPrincipal(pinDTO));

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setIdentifier(identifier);
        tokenEntity.setToken(token);
        tokenEntity.setStatus(1);
        tokenEntity.setCreatedAt(new Date());
        tokenEntity.setCreatedBy("API");

//        List<TokenEntity> list = tokenEntityRepo.findByIdentifierAndStatus(identifier, 1);
//        list.forEach(l -> {
//            l.setStatus(0);
//            tokenEntityRepo.save(l);
//        });

        tokenEntityRepo.save(tokenEntity);

        return token;
    }
    private UserPrincipal getUserPrincipal(PinDTO pinDTO) {

        return new UserPrincipal(pinDTO);
    }
    public String encrypt(String request) throws Exception {
        Gson gson = new Gson();
        if(!enableTestEncrypt){
            HashMap<String, String> map = new HashMap<>();
            map.put("error", "This function is disabled");
            return gson.toJson(map);
        }
        JsonObject object = gson.fromJson(request, JsonObject.class);
        try {
            JsonObject result = new JsonObject();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                result.addProperty(entry.getKey(), encryptService.encrypt(entry.getValue().getAsString()));
            }
            return gson.toJson(result);
        } catch (Exception e) {
            log.info("Error " + e.getLocalizedMessage());
            object.addProperty("error", "Error " + e.getLocalizedMessage());
            return gson.toJson(object);
        }
    }
    @Override
    public ResponseDTO fetchCustomerDetails(String nationalId) {
        ResponseDTO response = new ResponseDTO();

        if (nationalId == null || nationalId.isEmpty()) {
            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Invalid request data");

            log.warn("Invalid request data for fetching customer details");
            return response;
        }
        try {
            Optional<CustomerProfile> customerProfileOpt = customerRepo.findByNationalId(nationalId);
            if (customerProfileOpt.isPresent()) {
                CustomerProfile customerProfile = customerProfileOpt.get();

                response.setStatus(apiStatusProperties.getApiSuccessCode());
                response.setStatusDesc(apiStatusProperties.getApiSuccessCodeDesc());
                response.setRequestId(requestId);
                response.setConversationId(utils.genConversationId());
                response.setChannel(channel);
                response.setTimestamp(utils.genTimestamp());
                response.setStatusMessage("Customer details fetched successfully");
                response.setAdditionalData(customerProfile); // Ensure ResponseDTO has a field for this

                log.info("Customer details fetched for ID: {}", nationalId);
            } else {
                response.setStatus(apiStatusProperties.getApiErrorCode());
                response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
                response.setStatusMessage("Customer not found");

                log.warn("Customer not found for ID: {}", nationalId);
            }
        } catch (Exception e) {
            log.error("Exception occurred while fetching customer details: {}", e.getMessage(), e);

            response.setStatus(apiStatusProperties.getApiErrorCode());
            response.setStatusDesc(apiStatusProperties.getApiErrorCodeDesc());
            response.setStatusMessage("Exception occurred while fetching customer details");
        }
        return response;
    }
}

