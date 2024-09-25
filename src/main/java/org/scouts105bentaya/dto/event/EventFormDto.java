package org.scouts105bentaya.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
public class EventFormDto {
    private Integer id;
    private Integer groupId;
    private String title;
    private String description;
    private String location;
    private String longitude;
    private String latitude;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private LocalDate localStartDate; //todo maybe it can be removed and use withzonesamelocal
    private LocalDate localEndDate;
    private boolean unknownTime;
    @NotNull(message = "Activating Attendance List")
    private boolean activateAttendanceList;
    private boolean activateAttendancePayment;
    private boolean closeAttendanceList;
}
