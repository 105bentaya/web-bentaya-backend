package org.scouts105bentaya.features.special_member.entity;

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
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.special_member.enums.SpecialMemberRole;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class SpecialMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private SpecialMemberRole role;
    @Column(nullable = false)
    private Integer roleCensus;
    private LocalDate agreementDate;
    private LocalDate awardDate;
    private String details;
    @Column(columnDefinition = "text")
    private String observations;

    @ManyToOne
    @JsonIgnore
    private Scout scout;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private SpecialMemberPerson person;

    @OneToMany(mappedBy = "specialMember")
    private List<SpecialMemberDonation> donations;
}
