package org.scouts105bentaya.features.jamboree_inscription.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class JamboreeInscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String census;
    private String participantType;
    private String surname;
    private String name;
    private String feltName;
    private String dni;
    private String passportNumber;
    private String nationality;
    private LocalDate birthDate;
    private String ageAtJamboree;
    private String gender;
    private String phoneNumber;
    private String email;
    private boolean resident;
    private String municipality;
    @Column(length = 511)
    private String address;
    private String cp;
    private String locality;

    private String bloodType;
    @Column(length = 2000)
    private String medicalData;
    @Column(length = 2000)
    private String medication;
    @Column(length = 2000)
    private String allergies;
    private boolean vaccineProgram;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "inscription")
    private List<JamboreeLanguage> languages;
    private String size;
    @Column(length = 2000)
    private String foodIntolerances;
    @Column(length = 2000)
    private String dietPreference;
    @Column(length = 2000)
    private String observations;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "inscription")
    private List<JamboreeContact> contacts;
}
