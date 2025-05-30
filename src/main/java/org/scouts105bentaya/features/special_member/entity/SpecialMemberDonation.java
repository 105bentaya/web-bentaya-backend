package org.scouts105bentaya.features.special_member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.special_member.DonationType;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class SpecialMemberDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationType type;
    private String inKindDonationType;
    private Integer amount;
    private String paymentType;
    private String bankAccount;
    @Column(length = 511)
    private String notes;
    @ManyToOne(optional = false)
    @JsonIgnore
    private SpecialMember specialMember;
}
