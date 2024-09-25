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
import org.scouts105bentaya.enums.Group;

import java.time.ZonedDateTime;
import java.util.List;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Group getGroupId() {
        return groupId;
    }

    public void setGroupId(Group groupId) {
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

    public boolean isUnknownTime() {
        return unknownTime;
    }

    public void setUnknownTime(boolean unknownTime) {
        this.unknownTime = unknownTime;
    }

    public List<Confirmation> getConfirmationList() {
        return confirmationList;
    }

    public void setConfirmationList(List<Confirmation> confirmationList) {
        this.confirmationList = confirmationList;
    }

    public boolean isActiveAttendanceList() {
        return activeAttendanceList;
    }

    public void setActiveAttendanceList(boolean activeAttendanceList) {
        this.activeAttendanceList = activeAttendanceList;
    }

    public boolean isActiveAttendancePayment() {
        return activeAttendancePayment;
    }

    public void setActiveAttendancePayment(boolean activeAttendancePayment) {
        this.activeAttendancePayment = activeAttendancePayment;
    }

    public boolean isClosedAttendanceList() {
        return closedAttendanceList;
    }

    public void setClosedAttendanceList(boolean closedAttendanceList) {
        this.closedAttendanceList = closedAttendanceList;
    }

    public boolean eventHasEnded() {
        return getEndDate().isBefore(ZonedDateTime.now());
    }
}
