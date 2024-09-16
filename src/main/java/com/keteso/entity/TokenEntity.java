package com.keteso.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name="MOK_AUTH_TOKENS", schema = "MOK")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class TokenEntity extends AuditModel {
    @Column(name="IDENTIFIER")
    private String identifier;
    @Column(name="TOKEN")
    private String token;
    @Column(name="STATUS")
    private int status;

}
