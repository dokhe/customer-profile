package com.keteso.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MOK_GENDER", schema = "MOK")
public class SlnGender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REC_NO")
    private Long recNo;
    @Column(name = "GENDER", nullable = false, length = 15)
    private String gender;
    @Column(name = "GENDER_ABBRV", nullable = false, length = 5)
    private String genderAbbrv;
}

