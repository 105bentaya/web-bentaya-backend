package org.scouts105bentaya.dto.event;

import java.time.ZonedDateTime;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Boolean getHasAttendance() {
        return hasAttendance;
    }

    public void setHasAttendance(Boolean hasAttendance) {
        this.hasAttendance = hasAttendance;
    }
}
