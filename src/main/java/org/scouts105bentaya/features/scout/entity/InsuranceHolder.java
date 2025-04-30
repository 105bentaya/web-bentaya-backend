package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class InsuranceHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @ManyToOne
    private ScoutContact contact;

    private String name;
    private String surname;

    @OneToOne(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private IdentificationDocument idDocument;

    private String phone;
    private String email;
}
