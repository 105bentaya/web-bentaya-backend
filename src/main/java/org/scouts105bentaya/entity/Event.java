package org.scouts105bentaya.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.enums.Group;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Group groupId;
    private String title;
    private String description;
    private String location;
    private String longitude;
    private String latitude;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private boolean unknownTime;
    private boolean activeAttendanceList;
    private boolean activeAttendancePayment;
    private boolean closedAttendanceList;
    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    private List<Confirmation> confirmationList;

    public boolean eventHasEnded() {
        return getEndDate().isBefore(ZonedDateTime.now());
    }
}
