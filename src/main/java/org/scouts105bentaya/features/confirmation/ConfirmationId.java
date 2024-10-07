package org.scouts105bentaya.features.confirmation;

import java.io.Serializable;
import java.util.Objects;

public class ConfirmationId implements Serializable {

    private Integer scout;

    private Integer event;

    public ConfirmationId() {
    }

    public ConfirmationId(Integer scout, Integer event) {
        this.scout = scout;
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmationId that = (ConfirmationId) o;
        return Objects.equals(scout, that.scout) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scout, event);
    }
}
