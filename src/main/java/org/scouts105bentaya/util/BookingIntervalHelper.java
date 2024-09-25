package org.scouts105bentaya.util;

import org.joda.time.Interval;
import org.scouts105bentaya.dto.booking.SimpleBookingDto;
import org.scouts105bentaya.entity.Booking;
import org.scouts105bentaya.enums.BookingStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.scouts105bentaya.enums.BookingStatus.FULLY_OCCUPIED;
import static org.scouts105bentaya.enums.BookingStatus.OCCUPIED;
import static org.scouts105bentaya.enums.BookingStatus.RESERVED;
import static org.scouts105bentaya.util.IntervalUtils.intervalFromBooking;
import static org.scouts105bentaya.util.IntervalUtils.jodaLocalDateTimeToJavaDateTime;
import static org.scouts105bentaya.util.IntervalUtils.mergeIntervalList;
import static org.scouts105bentaya.util.IntervalUtils.removeOverlappingSectionsFromSecondIntervalList;

public class BookingIntervalHelper {

    private final List<Booking> sameCenterBookings;
    private final Interval mainInterval;

    private List<Booking> overlappingBookingsIntervalList;

    List<Interval> fullyOccupiedIntervals;
    List<Interval> occupiedIntervals;
    List<Interval> reservedIntervals;

    //todo not working properyl, getting same ranges for occupied and fully occupied
//    Durante las siguientes fechas el centro está parcialmente ocupado, por lo que su reserva puede no ser aceptada si sobrepasa el aforo restante:
//        25/09 23:00 - 26/09 23:45
//    Durante las siguientes fechas el centro está totalmente ocupado, por lo que no se puede realizar la reserva:
//        25/09 23:00 - 26/09 23:45
    public BookingIntervalHelper(List<Booking> sameCenterBookings, Interval mainInterval) {
        if (sameCenterBookings.stream().map(Booking::getScoutCenter).distinct().count() > 1) {
            throw new RuntimeException("Not all bookings have same center");
        }
        this.mainInterval = mainInterval;
        this.sameCenterBookings = sameCenterBookings;
    }

    public boolean overlapsWithFullyOccupiedBooking() {
        this.overlappingBookingsIntervalList = sameCenterBookings.stream()
                .filter(booking -> booking.getStatus() == FULLY_OCCUPIED)
                .filter(this::bookingOverlapsWithMainInterval)
                .toList();

        return !adjustIntervalsToBooking(
                mergeIntervalList(getSortedIntervalListByBookingStatus(FULLY_OCCUPIED)), FULLY_OCCUPIED
        ).isEmpty();
    }

    public List<SimpleBookingDto> getOverlappingBookingIntervals() {
        this.overlappingBookingsIntervalList = sameCenterBookings.stream()
                .filter(this::bookingStatusIsNeeded)
                .filter(this::bookingOverlapsWithMainInterval)
                .toList();

        fullyOccupiedIntervals = getSortedIntervalListByBookingStatus(FULLY_OCCUPIED);
        occupiedIntervals = getSortedIntervalListByBookingStatus(OCCUPIED);
        reservedIntervals = getSortedIntervalListByBookingStatus(RESERVED);

        fullyOccupiedIntervals = mergeIntervalList(fullyOccupiedIntervals);
        occupiedIntervals = mergeIntervalList(occupiedIntervals);
        reservedIntervals = mergeIntervalList(reservedIntervals);

        occupiedIntervals = removeOverlappingSectionsFromSecondIntervalList(fullyOccupiedIntervals, occupiedIntervals);
        reservedIntervals = removeOverlappingSectionsFromSecondIntervalList(fullyOccupiedIntervals, reservedIntervals);

        List<SimpleBookingDto> result = new ArrayList<>();
        result.addAll(adjustIntervalsToBooking(fullyOccupiedIntervals, FULLY_OCCUPIED));
        result.addAll(adjustIntervalsToBooking(occupiedIntervals, OCCUPIED));
        result.addAll(adjustIntervalsToBooking(reservedIntervals, RESERVED));
        return result;
    }


    private List<Interval> getSortedIntervalListByBookingStatus(BookingStatus status) {
        return this.overlappingBookingsIntervalList.stream()
                .filter(booking -> booking.getStatus() == status)
                .map(IntervalUtils::intervalFromBooking)
                .sorted(Comparator.comparingLong(Interval::getStartMillis))
                .collect(Collectors.toList());
    }

    private List<SimpleBookingDto> adjustIntervalsToBooking(List<Interval> intervals, BookingStatus status) {
        return intervals.stream()
            .filter(interval -> interval.overlaps(mainInterval))
            .map(interval -> {
            if (interval.getStart().isBefore(mainInterval.getStart())) {
                interval = interval.withStart(mainInterval.getStart());
            }
            if (interval.getEnd().isAfter(mainInterval.getEnd())) {
                interval = interval.withEnd(mainInterval.getEnd());
            }
            return createIntervalDto(interval, status);
        }).toList();
    }

    private SimpleBookingDto createIntervalDto(Interval interval, BookingStatus status) {
        SimpleBookingDto dto = new SimpleBookingDto();
        dto.setStartDate(jodaLocalDateTimeToJavaDateTime(interval.getStart()));
        dto.setEndDate(jodaLocalDateTimeToJavaDateTime(interval.getEnd()));
        dto.setStatus(status);
        return dto;
    }

    private boolean bookingStatusIsNeeded(Booking booking) {
        BookingStatus status = booking.getStatus();
        return status == RESERVED ||
                status == OCCUPIED ||
                status == FULLY_OCCUPIED;
    }

    private boolean bookingOverlapsWithMainInterval(Booking booking) {
        Interval bookingInterval = intervalFromBooking(booking);
        return bookingInterval.overlap(mainInterval) != null && bookingInterval.toDurationMillis() > 0;
    }
}
