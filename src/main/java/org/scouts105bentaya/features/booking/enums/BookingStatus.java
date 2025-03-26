package org.scouts105bentaya.features.booking.enums;

public enum BookingStatus {
    NEW, RESERVED, OCCUPIED, CANCELED, REJECTED;

    public boolean reservedOrOccupied() {
        return this == RESERVED || this == OCCUPIED;
    }
}
