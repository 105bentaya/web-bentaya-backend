package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class PersonalData {
    @Id
    @JsonIgnore
    private Integer scoutId;

    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private IdentificationDocument idDocument;

    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String name;
    private String feltName;
    @Column(nullable = false)
    private LocalDate birthday;
    private String birthplace;
    private String birthProvince;
    private String nationality;
    private String address;
    private String city;
    private String province;
    private String phone;
    private String landline;
    private String email;
    private String shirtSize;
    private String residenceMunicipality;
    @Column(nullable = false)
    private String gender;
    private boolean imageAuthorization;

    @Column(columnDefinition = "text")
    private String observations;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ScoutFile> documents;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "scout_id")
    @JsonIgnore
    private Scout scout;
}
