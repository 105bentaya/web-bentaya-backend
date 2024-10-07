package org.scouts105bentaya.features.pre_scout.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PreScout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String surname;
    private String section;
    private String birthday;
    private String age;
    private String gender;
    private String dni;
    private boolean hasBeenInGroup;
    private String yearAndSection;
    private String medicalData;
    private String parentsName;
    private String relationship;
    private String phone;
    private String email;
    private String comment;
    private Integer priority;
    private String creationDate;
    private String parentsSurname;
    private String priorityInfo;
    private String size;
    private String inscriptionYear;
    @OneToOne(mappedBy = "preScout", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private PreScoutAssignation preScoutAssignation;
}