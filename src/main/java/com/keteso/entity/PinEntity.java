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
@Table(name="MOK_AUTH_PIN", schema = "MOK")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class PinEntity extends AuditModel {

    @Column(name= "CHANNEL_ID", nullable = false)
    private Long channelId;

    @Column(name="IDENTIFIER")
    private String identifier;

    @Column(name="PIN")
    private String pin;

    @Column(name="ATTEMPTS")
    private int attempts;

    @Column(name="STATUS")
    private int status;

    @Column(name= "RETRY_AT")
    private Date retryAt;

}
