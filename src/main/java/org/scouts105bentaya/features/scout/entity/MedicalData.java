package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.scout.enums.BloodType;

import java.util.List;

@Entity
@Getter
@Setter
public class MedicalData {
    @Id
    @JsonIgnore
    private Integer scoutId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodType bloodType;
    private String socialSecurityNumber;
    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private InsuranceHolder socialSecurityHolder;
    private String privateInsuranceNumber;
    private String privateInsuranceEntity;
    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private InsuranceHolder privateInsuranceHolder;

    @Column(columnDefinition = "text")
    private String foodIntolerances;
    @Column(columnDefinition = "text")
    private String foodAllergies;
    @Column(columnDefinition = "text")
    private String foodProblems;
    @Column(columnDefinition = "text")
    private String foodMedication;
    @Column(columnDefinition = "text")
    private String foodDiet;

    @Column(columnDefinition = "text")
    private String medicalIntolerances;
    @Column(columnDefinition = "text")
    private String medicalAllergies;
    @Column(columnDefinition = "text")
    private String medicalDiagnoses;
    @Column(columnDefinition = "text")
    private String medicalPrecautions;
    @Column(columnDefinition = "text")
    private String medicalMedications;
    @Column(columnDefinition = "text")
    private String medicalEmergencies;

    @Column(columnDefinition = "text")
    private String addictions;
    @Column(columnDefinition = "text")
    private String tendencies;
    @Column(columnDefinition = "text")
    private String records;
    @Column(columnDefinition = "text")
    private String bullyingProtocol;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<MemberFile> documents;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "scout_id")
    @JsonIgnore
    private Scout scout;
}
