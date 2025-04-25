package org.scouts105bentaya.features.scout.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class RealPersonalData extends PersonalData {
    private String surname;
    private String name;
    private String feltName;
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
    private String gender;
}
