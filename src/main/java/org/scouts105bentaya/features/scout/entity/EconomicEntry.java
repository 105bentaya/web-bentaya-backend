package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class EconomicEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Integer amount;
    //todo this four change with something lixe expense type????
    private String income;
    private String spending;
    private String account;
    @Column(nullable = false)
    private String type;

    @Column(length = 511)
    private String observations;

    @ManyToOne(optional = false)
    @JsonIgnore
    private EconomicData economicData;

}
