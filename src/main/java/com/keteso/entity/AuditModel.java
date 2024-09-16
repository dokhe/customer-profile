package com.keteso.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@MappedSuperclass
public class AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REC_NO")
    @JsonIgnore
    private Long recNo;

    //@Column(name = "UNQ_ID", unique = true, nullable = false, length = 255, updatable = false)
    //private String uniqueId;

    @Column(name = "CREATED_BY")
    @JsonIgnore
    private String createdBy;

    @Column(name = "UPDATED_BY")
    @JsonIgnore
    private String updatedBy;

    @Column(name = "CREATED_AT")
    @JsonIgnore
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    @JsonIgnore
    private Date updatedAt;

}
