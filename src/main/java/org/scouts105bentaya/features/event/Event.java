package org.scouts105bentaya.features.event;

import jakarta.annotation.Nullable;
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
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.shared.Group;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Accessors(chain = true)
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
    @Nullable
    private ZonedDateTime closeDateTime;
    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    private List<Confirmation> confirmationList;

    public boolean eventHasEnded() {
        return getEndDate().isBefore(ZonedDateTime.now());
    }

    public boolean eventAttendanceIsClosed() {
        if (activeAttendanceList) {
            if (closedAttendanceList) return true;
            if (closeDateTime == null) return eventHasEnded();
            return getCloseDateTime().isBefore(ZonedDateTime.now());
        }
        return false;
    }
}
