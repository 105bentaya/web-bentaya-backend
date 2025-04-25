package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.scout.enums.MemberRole;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class MemberRoleInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Member member;

    //notnull?
    private Integer roleCensus;

    private String reason;

    private LocalDate date;

    //Fecha Acuerdo Hon
    //Fecha Entrega Hon
    //Lugar Honor o Familia Fund.

    private String observations;
}
