package org.scouts105bentaya.features.confirmation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.scout.entity.Scout;

@Entity
@IdClass(ConfirmationId.class)
@Getter
@Setter
public class Confirmation {
    @Id
    @ManyToOne
    @JoinColumn(name = "scout_id", referencedColumnName = "id")
    private Scout scout;
    @Id
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    private Boolean attending;
    private String text;
    private Boolean payed;
}