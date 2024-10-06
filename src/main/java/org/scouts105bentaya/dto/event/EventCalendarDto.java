package org.scouts105bentaya.dto.event;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class EventCalendarDto {
    private Integer id;
    private Integer groupId;
    private String title;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private boolean unknownTime;
}
