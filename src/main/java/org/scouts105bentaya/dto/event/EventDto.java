package org.scouts105bentaya.dto.event;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class EventDto {
    private Integer id;
    private Integer groupId;
    private String title;
    private String description;
    private String location;
    private String longitude;
    private String latitude;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private boolean unknownTime;
    private Boolean hasAttendance;
}
