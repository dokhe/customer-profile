package com.keteso.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name="MOK_AUTH_CHANNELS", schema = "MOK")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class ChannelEntity extends AuditModel {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "REC_NO", nullable = false)
//    private Long recNumber;
    @Column(name="CHANNEL_CODE")
    private String channelCode;
    @Column(name="CHANNEL_NAME")
    private String channelName;
    @Column(name="OTP_MAX_LIFE") //Maximum OTP life in minutes
    private int otpMaxLife;
    @Column(name="PIN_MAX_ATTEMPTS") //Maximum PIN Attempts in counts
    private int pinMaxAttempts;
    @Column(name="PIN_RETRY_DELAY") //Delay retry pin in minutes
    private int pinRetryDelay;
    @Column(name="PIN_NO_REPEAT_LAST") //Do not repeat last pins in counts
    private int pinNoRepeatLast;
}
