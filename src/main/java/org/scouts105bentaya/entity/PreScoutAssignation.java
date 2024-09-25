package org.scouts105bentaya.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.constraint.IsUnit;
import org.scouts105bentaya.enums.Group;

import java.time.ZonedDateTime;

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

    public void setPreScoutId(Integer preScoutId) {
        this.preScoutId = preScoutId;
    }

    public Integer getPreScoutId() {
        return preScoutId;
    }

    public PreScout getPreScout() {
        return preScout;
    }

    public void setPreScout(PreScout preScout) {
        this.preScout = preScout;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ZonedDateTime getAssignationDate() {
        return assignationDate;
    }

    public void setAssignationDate(ZonedDateTime assignationDate) {
        this.assignationDate = assignationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Group getGroupId() {
        return groupId;
    }

    public void setGroupId(Group groupId) {
        this.groupId = groupId;
    }
}
