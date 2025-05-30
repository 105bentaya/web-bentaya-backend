package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class EconomicData {
    @Id
    @JsonIgnore
    private Integer scoutId;

    private String iban;
    private String bank;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<ScoutFile> documents;

    @OneToMany(mappedBy = "economicData")
    private List<EconomicEntry> entries;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "scout_id")
    @JsonIgnore
    private Scout scout;
}
