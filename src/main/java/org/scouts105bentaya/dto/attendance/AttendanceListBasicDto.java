package org.scouts105bentaya.dto.attendance;

import java.time.ZonedDateTime;

public class AttendanceListBasicDto {

    private Integer eventId;

    private ZonedDateTime eventStartDate;

    private ZonedDateTime eventEndDate;

    private String eventTitle;

    private int affirmativeConfirmations;

    private int negativeConfirmations;

    private int notRespondedConfirmations;

    private Integer affirmativeAndPayedConfirmations;

    private boolean eventHasPayment;

    private boolean eventIsClosed;

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

    public int getAffirmativeConfirmations() {
        return affirmativeConfirmations;
    }

    public void setAffirmativeConfirmations(int affirmativeConfirmations) {
        this.affirmativeConfirmations = affirmativeConfirmations;
    }

    public int getNegativeConfirmations() {
        return negativeConfirmations;
    }

    public void setNegativeConfirmations(int negativeConfirmations) {
        this.negativeConfirmations = negativeConfirmations;
    }

    public int getNotRespondedConfirmations() {
        return notRespondedConfirmations;
    }

    public void setNotRespondedConfirmations(int notRespondedConfirmations) {
        this.notRespondedConfirmations = notRespondedConfirmations;
    }

    public Integer getAffirmativeAndPayedConfirmations() {
        return affirmativeAndPayedConfirmations;
    }

    public void setAffirmativeAndPayedConfirmations(Integer affirmativeAndPayedConfirmations) {
        this.affirmativeAndPayedConfirmations = affirmativeAndPayedConfirmations;
    }

    public void setEventStartDate(ZonedDateTime eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public ZonedDateTime getEventStartDate() {
        return eventStartDate;
    }

    public ZonedDateTime getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(ZonedDateTime eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public boolean isEventHasPayment() {
        return eventHasPayment;
    }

    public void setEventHasPayment(boolean eventHasPayment) {
        this.eventHasPayment = eventHasPayment;
    }

    public boolean isEventIsClosed() {
        return eventIsClosed;
    }

    public void setEventIsClosed(boolean eventIsClosed) {
        this.eventIsClosed = eventIsClosed;
    }

    public void incrementAffirmativeConfirmations() {
        this.affirmativeConfirmations++;
    }

    public void incrementNegativeConfirmations() {
        this.negativeConfirmations++;
    }

    public void incrementNotRespondedConfirmations() {
        this.notRespondedConfirmations++;
    }

    public void incrementAffirmativeAndPayedConfirmations() {
        if (this.affirmativeAndPayedConfirmations != null) {
            this.affirmativeAndPayedConfirmations++;
        } else {
            this.affirmativeAndPayedConfirmations = 1;
        }
    }
}
