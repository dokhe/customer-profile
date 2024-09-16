package com.keteso.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class PrimaryData {
    private String nationalId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dateOfBirth;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNumber;
    private String requestId;
    private String channelCode;
    private String accountNumber;
    Documents documents;
}
