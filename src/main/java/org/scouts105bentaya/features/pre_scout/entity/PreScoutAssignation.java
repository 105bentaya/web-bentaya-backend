package org.scouts105bentaya.features.pre_scout.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.group.Group;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Accessors(chain = true)
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
    @ManyToOne
    @Nullable
    private Group group;
}
