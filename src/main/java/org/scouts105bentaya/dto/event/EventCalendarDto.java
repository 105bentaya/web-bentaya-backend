package org.scouts105bentaya.dto.event;

import java.time.ZonedDateTime;

public class EventCalendarDto {

    private Integer id;

    private Integer groupId;

    private String title;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    private boolean unknownTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isUnknownTime() {
        return unknownTime;
    }

    public void setUnknownTime(boolean unknownTime) {
        this.unknownTime = unknownTime;
    }
}
