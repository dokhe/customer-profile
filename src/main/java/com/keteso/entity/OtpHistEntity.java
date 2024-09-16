package com.keteso.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name="MOK_AUTH_OTP_HIST", schema = "MOK")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class OtpHistEntity extends AuditModel {

    @Column(name= "OTP_ID", nullable = false)
    private Long otpId;

    @Column(name= "CHANNEL_ID", nullable = false)
    private Long channelId;

    @Column(name="IDENTIFIER")
    private String identifier;

    @Column(name="OTP")
    private String otp;

    @Column(name="EXPIRY_DATE_TIME")
    private Date expiryDateTime;

    @Column(name="ATTEMPTS")
    private int attempts;

    @Column(name="STATUS")
    private int status;

    @Column(name= "RETRY_AT")
    private Date retryAt;

}
