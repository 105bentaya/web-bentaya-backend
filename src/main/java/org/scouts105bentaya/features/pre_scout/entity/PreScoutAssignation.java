package org.scouts105bentaya.features.pre_scout.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.shared.constraint.IsUnit;
import org.scouts105bentaya.shared.Group;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
public class PreScoutAssignation {
    @Id
    private Integer preScoutId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "pre_scout_id")
    private PreScout preScout;
    @NotNull
    private Integer status;
    @NotNull
    private ZonedDateTime assignationDate;
    private String comment;
    @IsUnit
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Group groupId;
}
