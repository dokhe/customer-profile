package com.keteso.webClient;

import com.google.gson.Gson;
import com.keteso.configs.WebClientConfigs;
import com.keteso.model.CustomerProfileRequest;
import com.keteso.model.outgoingRequest.IprsRequest;
import com.keteso.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.Collections;

@Service
@Slf4j
@EnableAsync
public class WebClientImpl {

    @Autowired
    Utils utils;
    @Value("${sms.serverUrl}")
    String serverUrl;
    @Value("${sms.username}")
    String username;
    @Value("${sms.password}")
    String password;
    @Value("${sms.action}")
    String action;
    @Value("${sms.messageType}")
    String messageType;
    @Value("${sms.enabled}")
    Boolean smsEnabled;
    @Value("${iprs.external.url}")
    String iprsExternalUrl;
    @Value("${ocp.internal.url}")
    String ocpInternalUrl;
    @Value("${request.internal.featureCode}")
    String featureCode;
    @Value("${request.internal.featureName}")
    String featureName;
    @Value("${request.internal.serviceCode}")
    String serviceCode;
    @Value("${request.internal.serviceName}")
    String serviceName;
    @Value("${request.internal.serviceSubCategory}")
    String serviceSubCategory;
    @Value("${request.internal.minorServiceVersion}")
    String minorServiceVersion;
    @Value("${request.internal.channelCode}")
    String channelCode;
    @Value("${request.internal.channelName}")
    String channelName;
    @Value("${request.internal.routeCode}")
    String routeCode;
    @Value("${request.internal.serviceMode}")
    String serviceMode;
    @Value("${request.internal.subscribeEvents}")
    String subscribeEvents;
    @Value("${request.requestPayload.additionalData.companyCode}")
    String companyCode;
    @Value("${request.iprs.featureCode}")
    String iprsFeatureCode;
    @Value("${request.iprs.featureName}")
    String iprsFeatureName;
    @Value("${request.iprs.serviceCode}")
    String iprsServiceCode;
    @Value("${request.iprs.serviceName}")
    String iprsServiceName;
    @Value("${request.iprs.serviceSubCategory}")
    String iprsServiceSubCategory;
    @Value("${request.iprs.minorServiceVersion}")
    String iprsMinorServiceVersion;
    @Value("${request.iprs.channelCode}")
    String iprsChannelCode;
    @Value("${request.iprs.channelName}")
    String iprsChannelName;
    @Value("${request.iprs.routeCode}")
    String iprsRouteCode;
    @Value("${request.iprs.serviceMode}")
    String iprsServiceMode;
    @Value("${request.iprs.subscribeEvents}")
    String iprsSubscribeEvents;
    @Value("${request.payload.additionalData.companyCode}")
    String iprsCompanyCode;

    @Value("${req.hd.channel}")
    String channel;
    @Value("${request.internal.api-username}")
    String internalApiUsername;

    @Value("${request.internal.api-password}")
    String internalApiPassword;

    @Value("${request.iprs.api-username}")
    String iprsApiUsername;

    @Value("${request.iprs.api-password}")
    String iprsApiPassword;

    @Async
    public void scheduleSendSms(String mobileNumber, String sms) {
        if (!smsEnabled) {
            log.info("SMS notifications not enabled.");
            return;
        }
        try {
            log.info("inside scheduleSendSms to " + mobileNumber);
            WebClientConfigs webClientConfigs = new WebClientConfigs();
            WebClient webClient;

            webClient = webClientConfigs.createWebClient();

            webClient.put()
                    .uri(serverUrl, uri -> uri.queryParam("action", action)
                            .queryParam("username", username)
                            .queryParam("password", password)
                            .queryParam("channel",channel)
                            .queryParam("recipient", mobileNumber)
                            .queryParam("messagetype", messageType)
                            .queryParam("messagedata", sms)
                            .build())
                    .accept(MediaType.valueOf(MediaType.ALL_VALUE))
                    .retrieve()
                    .toEntity(String.class)
                    .subscribe(
                            responseEntity -> {
                                // Handle the response entity
                                log.info("Response: " + responseEntity.getBody());
                            },
                            error -> {
                                // Handle errors
                                log.error("Error: " + error.getMessage());
                            }
                    );


            //log.info("Resposnse = {} ", resp.getBody());

        } catch (Exception e) {
            log.info("Error occurred {}", e.getLocalizedMessage());
//            e.printStackTrace();
        }
    }
    public ResponseEntity<?> customerInternalCheck(CustomerProfileRequest customerProfileRequest) {
        try {
            WebClientConfigs webClientConfigs = new WebClientConfigs();
            WebClient webClient = webClientConfigs.createWebClient();

            IprsRequest iprsRequest = new IprsRequest();
            iprsRequest.setHeader(createHeader());
            iprsRequest.setRequestPayload(createRequestPayload(customerProfileRequest));

            String requestBody = new Gson().toJson(iprsRequest);
            log.info("Internal URL " + ocpInternalUrl);
            log.info("Internal Request = " + requestBody);

            ResponseEntity<String> respEntity = webClient.post()
                    .uri(ocpInternalUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((internalApiUsername + ":" + internalApiPassword).getBytes()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            return respEntity;

        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException code = " + e.getStatusCode());
            log.error("WebClientResponseException = " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());

        } catch (Exception e) {
            log.error("Exception occurred {}", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Helper method to create Header object
    private IprsRequest.Header createHeader() {
        IprsRequest.Header header = new IprsRequest.Header();
        header.setMessageID(utils.generateMessageId());
        header.setFeatureCode(featureCode);
        header.setFeatureName(featureName);
        header.setServiceCode(serviceCode);
        header.setServiceName(serviceName);
        header.setServiceSubCategory(serviceSubCategory);
        header.setMinorServiceVersion(minorServiceVersion);
        header.setChannelCode(channelCode);
        header.setChannelName(channelName);
        header.setRouteCode(routeCode);
        header.setTimeStamp(utils.genTimestamp());
        header.setServiceMode(serviceMode);
        header.setSubscribeEvents(subscribeEvents);
        return header;
    }
    private IprsRequest.IprsRequestPayload createRequestPayload(CustomerProfileRequest customerProfileRequest) {
        IprsRequest.IprsRequestPayload requestPayload = new IprsRequest.IprsRequestPayload();
        IprsRequest.IprsRequestPayload.PrimaryData primaryData = new IprsRequest.IprsRequestPayload.PrimaryData();
        IprsRequest.IprsRequestPayload.AdditionalData additionalData = new IprsRequest.IprsRequestPayload.AdditionalData();

        primaryData.setBusinessKey(customerProfileRequest.getPrimaryData().getNationalId());
        primaryData.setBusinessKeyType(customerProfileRequest.getPrimaryData().getAccountNumber());

        if (companyCode == null || companyCode.isEmpty()) {
            log.error("Company code is null or empty");
            companyCode = "defaultCompanyCode";
        }
        additionalData.setCompanyCode(companyCode);

        // Set additionalData as a single object
        requestPayload.setAdditionalData(Collections.singletonList(additionalData));
        return requestPayload;
    }
    public ResponseEntity<?> customerIprsCheck(CustomerProfileRequest customerProfileRequest) {
        try {
            WebClientConfigs webClientConfigs = new WebClientConfigs();
            WebClient webClient = webClientConfigs.createWebClient();

            IprsRequest iprsRequest = new IprsRequest();
            iprsRequest.setHeader(createHeaderIprs());
            iprsRequest.setRequestPayload(createRequestPayloadIprs(customerProfileRequest));

            String requestBody = new Gson().toJson(iprsRequest);
            log.info("IPRS External URL " + iprsExternalUrl);
            log.info("IPRS External Request = " + requestBody);

            ResponseEntity<String> respEntity = webClient.post()
                    .uri(iprsExternalUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((iprsApiUsername + ":" + iprsApiPassword).getBytes()))
                    .bodyValue(requestBody)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            return respEntity;

        } catch (WebClientResponseException e) {
            log.error("WebClientResponseException code = " + e.getStatusCode());
            log.error("WebClientResponseException = " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());

        } catch (Exception e) {
            log.error("Exception occurred {}", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Helper method to create Header object
    private IprsRequest.Header createHeaderIprs() {
        IprsRequest.Header header = new IprsRequest.Header();
        header.setMessageID(utils.generateMessageId());
        header.setFeatureCode(iprsFeatureCode);
        header.setFeatureName(iprsFeatureName);
        header.setServiceCode(iprsServiceCode);
        header.setServiceName(iprsServiceName);
        header.setServiceSubCategory(iprsServiceSubCategory);
        header.setMinorServiceVersion(iprsMinorServiceVersion);
        header.setChannelCode(iprsChannelCode);
        header.setChannelName(iprsChannelName);
        header.setRouteCode(iprsRouteCode);
        header.setTimeStamp(utils.genTimestamp());
        header.setServiceMode(iprsServiceMode);
        header.setSubscribeEvents(iprsSubscribeEvents);
        return header;
    }
    private IprsRequest.IprsRequestPayload createRequestPayloadIprs(CustomerProfileRequest customerProfileRequest) {
        IprsRequest.IprsRequestPayload requestPayload = new IprsRequest.IprsRequestPayload();
        IprsRequest.IprsRequestPayload.PrimaryData primaryData = new IprsRequest.IprsRequestPayload.PrimaryData();
        IprsRequest.IprsRequestPayload.AdditionalData additionalData = new IprsRequest.IprsRequestPayload.AdditionalData();

        primaryData.setBusinessKey(customerProfileRequest.getPrimaryData().getNationalId());
        primaryData.setBusinessKeyType(customerProfileRequest.getPrimaryData().getDocuments().getDocumentNumber());

        if (iprsCompanyCode == null || iprsCompanyCode.isEmpty()) {
            log.error("IPRS Company code is null or empty");
            iprsCompanyCode = "defaultIprsCompanyCode";
        }
        additionalData.setCompanyCode(iprsCompanyCode);

        // Set additionalData as a single object
        requestPayload.setAdditionalData(Collections.singletonList(additionalData));

        return requestPayload;
    }

}
