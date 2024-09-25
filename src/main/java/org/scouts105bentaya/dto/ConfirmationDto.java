package org.scouts105bentaya.dto;

public class ConfirmationDto {

    private Integer scoutId;

    private Integer eventId;

    private Boolean attending;

    private String text;

    private Boolean payed;

    public Integer getScoutId() {
        return scoutId;
    }

    public void setScoutId(Integer scoutId) {
        this.scoutId = scoutId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Boolean getAttending() {
        return attending;
    }

    public void setAttending(Boolean attending) {
        this.attending = attending;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }
}
