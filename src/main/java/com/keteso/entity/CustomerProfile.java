package com.keteso.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MOK_CUSTOMER", schema = "MOK")
public class CustomerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REC_NO")
    @JsonIgnore
    private Long recNo;

    @Column(name = "CUSTOMER_IDENTIFIER", nullable = false, length = 100)
    private UUID customerIdentifier;

    @Column(name = "IDENTIFICATION_NO", nullable = false, length = 100)
    private String nationalId;

    @ManyToOne
    @JoinColumn(name = "IDENTIFICATION_TYPE", nullable = false)
    private SlnDocuments identificationType;

    @Column(name = "DOCUMENT_NO", nullable = false, length = 30)
    private String documentNumber;

    @Column(name = "MSISDN", nullable = false, length = 30)
    private String mobileNumber;

    @Column(name = "DOB")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "FIRST_NAME", nullable = false, length = 30)
    private String firstName;

    @Column(name = "MIDDLE_NAME", length = 30)
    private String middleName;

    @Column(name = "LAST_NAME", nullable = false, length = 30)
    private String lastName;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "STATUS")
    private Integer status; // Changed from Number to Integer

    @ManyToOne
    @JoinColumn(name = "SLN_GENDER", nullable = false)
    private SlnGender slnGender;
}



