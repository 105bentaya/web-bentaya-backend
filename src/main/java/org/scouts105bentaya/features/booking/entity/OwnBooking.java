package org.scouts105bentaya.features.booking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.group.Group;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class OwnBooking extends Booking {
    @ManyToOne
    Group group;
}
