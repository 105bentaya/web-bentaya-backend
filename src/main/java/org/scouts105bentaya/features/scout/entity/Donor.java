package org.scouts105bentaya.features.scout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.scout.enums.PersonType;

@Entity
@Getter
@Setter
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private Integer roleCensus;

    @OneToOne
    private Scout scout;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonType type;
    private String name;
    private String surname;
    private String companyName;
    @OneToOne(optional = false)
    private IdentificationDocument idDocument;
    private String phone;
    private String email;
    @Column(nullable = false, length = 511)
    private String address;
}
