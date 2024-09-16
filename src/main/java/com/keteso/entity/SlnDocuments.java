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
@Table(name = "MOK_DOCUMENTS", schema = "MOK")
public class SlnDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REC_NO")
    private Long recNo;
    private Long isIdentification = 0L;
}

