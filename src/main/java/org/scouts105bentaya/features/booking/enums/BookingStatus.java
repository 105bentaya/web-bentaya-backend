package org.scouts105bentaya.features.booking.enums;

public enum BookingStatus {
    NEW, RESERVED, FULLY_OCCUPIED, OCCUPIED, LEFT, FINISHED, CANCELED, REJECTED;

    public boolean shouldShowInInformationCalendar() {
        return this == RESERVED || this == FULLY_OCCUPIED || this == OCCUPIED;
    }
}
