package org.scouts105bentaya.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@IdClass(ConfirmationId.class)
public class Confirmation {

    @Id
    @ManyToOne
    @JoinColumn (name = "scout_id", referencedColumnName = "id")
    private Scout scout;

    @Id
    @ManyToOne
    @JoinColumn (name = "event_id", referencedColumnName = "id")
    private Event event;

    private Boolean attending;

    private String text;

    private Boolean payed;

    public Scout getScout() {
        return scout;
    }

    public void setScout(Scout scout) {
        this.scout = scout;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
