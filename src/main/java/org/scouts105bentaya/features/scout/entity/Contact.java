package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
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

@Entity
@Getter
@Setter
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @Column(nullable = false)
    private String name;
    private String surname;
    private String relationship;
    private boolean donor;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private IdentificationDocument idDocument;

    private String phone;
    private String email;
    private String studies;
    private String profession;
    private String companyName;

    @Column(columnDefinition = "text")
    private String observations;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Scout scout;
}
