package com.keteso.utils;

import com.google.gson.Gson;
import com.keteso.model.CustomerProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerProfileLogger {

    private static final Logger log = LoggerFactory.getLogger(CustomerProfileLogger.class);
    private static final Gson gson = new Gson();

    public static void logCustomerProfile(CustomerProfileRequest customerProfile) {
        // Serialize the customerProfile object to a JSON string
        String customerProfileJson = gson.toJson(customerProfile);
        // Log the JSON string
        log.info("CustomerProfile saved: " + customerProfileJson);
    }
}
