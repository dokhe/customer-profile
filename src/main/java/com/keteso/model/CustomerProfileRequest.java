package com.keteso.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerProfileRequest {
    private String messageId;
    PrimaryData primaryData;
    private List<AdditionalData> additionalData;
}

