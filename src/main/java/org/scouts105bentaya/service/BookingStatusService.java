package org.scouts105bentaya.service;

import org.scouts105bentaya.entity.Booking;

public interface BookingStatusService {

    void saveFromForm(Booking booking);

    Booking bookingFromNewToRejected(Booking current, String reason);

    Booking bookingFromNewToReserved(Booking currentBooking, String observations, Float price);

    Booking bookingCanceled(Booking currentBooking, String observations);

    Booking bookingFromReservedToReservedByUser(Booking currentBooking);

    Booking bookingFromReservedToReservedByManager(Booking currentBooking, String observations);

    Booking bookingFromReservedToOccupied(Booking currentBooking, String observations, boolean fully);

    Booking bookingFromOccupiedToLeftByUser(Booking currentBooking, String documents);

    Booking bookingToFinished(Booking currentBooking, String observations);
}
