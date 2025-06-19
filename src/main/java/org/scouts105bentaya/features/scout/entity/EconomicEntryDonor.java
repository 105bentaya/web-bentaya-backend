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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.scout.enums.PersonType;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class EconomicEntryDonor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer economicEntryId;

    @Column(nullable = false)
    private String name;

    @Column
    private String surname;

    @OneToOne(optional = false, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private IdentificationDocument idDocument;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "economic_entry_id")
    @JsonIgnore
    private EconomicEntry economicEntry;
}
