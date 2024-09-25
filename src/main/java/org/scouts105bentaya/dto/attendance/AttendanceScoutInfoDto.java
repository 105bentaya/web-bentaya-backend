package org.scouts105bentaya.dto.attendance;

import java.time.ZonedDateTime;

public class AttendanceScoutInfoDto {

    private Integer eventId;

    private ZonedDateTime eventStartDate;

    private ZonedDateTime eventEndDate;

    private String eventTitle;

    private Boolean attending;

    private Boolean payed;

    private boolean closed;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Boolean getAttending() {
        return attending;
    }

    public void setAttending(Boolean attending) {
        this.attending = attending;
    }

    public ZonedDateTime getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(ZonedDateTime eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public ZonedDateTime getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(ZonedDateTime eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
