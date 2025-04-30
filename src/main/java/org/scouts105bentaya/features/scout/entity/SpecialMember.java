package org.scouts105bentaya.features.scout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.scouts105bentaya.features.scout.enums.SpecialMemberRole;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class SpecialMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private SpecialMemberRole role;
    @Column(nullable = false)
    private Integer roleCensus;
    private LocalDate agreementDate;
    private LocalDate awardDate;
    private String details;
    @Column(columnDefinition = "text")
    private String observations;

    @ManyToOne
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
}
