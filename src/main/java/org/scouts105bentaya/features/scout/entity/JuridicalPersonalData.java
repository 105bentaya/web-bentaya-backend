package org.scouts105bentaya.features.scout.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JuridicalPersonalData extends PersonalData {
    private String companyName;

    @OneToOne
    private JuridicalRepresentative representative;
}
