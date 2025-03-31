package org.scouts105bentaya.features.booking.util;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.scouts105bentaya.features.booking.dto.data.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.scouts105bentaya.features.booking.util.IntervalUtils.intervalFromBooking;
import static org.scouts105bentaya.features.booking.util.IntervalUtils.jodaLocalDateTimeToJavaDateTime;

@Slf4j
public final class BookingIntervalHelper {

    private BookingIntervalHelper() {
    }

    public static boolean overlapsWithFullyOccupiedBooking(List<Booking> sameCenterBookings, Interval intervalToCheck) {
        validateBookingCenters(sameCenterBookings);
        return sameCenterBookings.stream()
            .filter(booking -> booking.getStatus() == BookingStatus.OCCUPIED && booking.isExclusiveReservation())
            .anyMatch(booking -> bookingOverlapsWithMainInterval(booking, intervalToCheck));
    }

    public static List<BookingDateAndStatusDto> getOverlappingBookingIntervals(List<Booking> sameCenterBookings, Interval intervalToCheck) {
        validateBookingCenters(sameCenterBookings);
        List<Booking> overlappingBookings = sameCenterBookings.stream()
            .filter(booking -> booking.getStatus().reservedOrOccupied())
            .filter(booking -> bookingOverlapsWithMainInterval(booking, intervalToCheck))
            .toList();

        List<Interval> fullyOccupied = bookingToInterval(overlappingBookings, BookingStatus.OCCUPIED, true, intervalToCheck);

        List<Interval> partiallyOccupied = bookingToInterval(overlappingBookings, BookingStatus.OCCUPIED, false, intervalToCheck);
        partiallyOccupied = IntervalUtils.removeIntervalListOverlappingSectionsWithIntervalList(fullyOccupied, partiallyOccupied);


        List<Interval> reserved = bookingToInterval(overlappingBookings, BookingStatus.RESERVED, null, intervalToCheck);
        reserved = IntervalUtils.removeIntervalListOverlappingSectionsWithIntervalList(Stream.concat(fullyOccupied.stream(), partiallyOccupied.stream()).toList(), reserved);

        List<BookingDateAndStatusDto> result = new ArrayList<>();
        result.addAll(intervalListToBookingDateAndStatusList(fullyOccupied, BookingStatus.OCCUPIED, true, intervalToCheck));
        result.addAll(intervalListToBookingDateAndStatusList(partiallyOccupied, BookingStatus.OCCUPIED, false, intervalToCheck));
        result.addAll(intervalListToBookingDateAndStatusList(reserved, BookingStatus.RESERVED, null, intervalToCheck));
        return result;
    }

    private static List<BookingDateAndStatusDto> intervalListToBookingDateAndStatusList(List<Interval> intervals, BookingStatus status, Boolean fullyOccupied, Interval mainInterval) {
        return IntervalUtils.mergeIntervalList(intervals).stream()
            .map(interval -> adjustIntervalsToBooking(interval, mainInterval))
            .map(interval -> new BookingDateAndStatusDto(
                jodaLocalDateTimeToJavaDateTime(interval.getStart()),
                jodaLocalDateTimeToJavaDateTime(interval.getEnd()),
                status,
                fullyOccupied
            )).toList();
    }

    private static Interval adjustIntervalsToBooking(Interval intervalToAdjust, Interval mainInterval) {
        if (intervalToAdjust.getStart().isBefore(mainInterval.getStart())) {
            intervalToAdjust = intervalToAdjust.withStart(mainInterval.getStart());
        }
        if (intervalToAdjust.getEnd().isAfter(mainInterval.getEnd())) {
            intervalToAdjust = intervalToAdjust.withEnd(mainInterval.getEnd());
        }
        return intervalToAdjust;
    }

    private static List<Interval> bookingToInterval(List<Booking> bookings, BookingStatus status, Boolean exclusiveReservation, Interval mainInterval) {
        return bookings.stream()
            .filter(predicateFromStatus(status, exclusiveReservation))
            .map(IntervalUtils::intervalFromBooking)
            .map(interval -> adjustIntervalsToBooking(interval, mainInterval))
            .toList();
    }

    private static Predicate<Booking> predicateFromStatus(BookingStatus status, Boolean exclusiveReservation) {
        if (status == BookingStatus.RESERVED) return booking -> booking.getStatus() == BookingStatus.RESERVED;
        return booking -> booking.getStatus() == status && booking.isExclusiveReservation() == exclusiveReservation;
    }

    private static boolean bookingOverlapsWithMainInterval(Booking booking, Interval mainInterval) {
        Interval bookingInterval = intervalFromBooking(booking);
        return bookingInterval.overlap(mainInterval) != null && bookingInterval.toDurationMillis() > 0;
    }

    private static void validateBookingCenters(List<Booking> bookings) {
        if (bookings.stream().map(Booking::getScoutCenter).distinct().count() > 1) {
            throw new IllegalArgumentException("Bookings are not of same scout center");
        }
    }
}
