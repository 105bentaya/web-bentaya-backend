package org.scouts105bentaya.features.booking.util;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.joda.time.Interval;
import org.junit.jupiter.api.Test;
import org.scouts105bentaya.features.booking.dto.data.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.scout_center.entity.ScoutCenter;

import java.time.LocalDateTime;
import java.util.List;

class BookingIntervalHelperTest {

    private ScoutCenter newCenter(Integer id) {
        return new ScoutCenter().setId(id);
    }

    @Test
    void overlapsWithFullyOccupiedBookingAndBookingsAreFromDifferentCentersThrowsError() {
        //given
        var bookingList = List.of(
            new GeneralBooking().setScoutCenter(newCenter(1)),
            new OwnBooking().setScoutCenter(newCenter(2))
        );
        var interval = new Interval(100, 200);

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> BookingIntervalHelper.overlapsWithFullyOccupiedBooking(bookingList, interval);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Bookings are not of same scout center");
    }

    @Test
    void overlapsWithFullyOccupiedBookingReturnsTrue() {
        //given
        var scoutCenter = newCenter(1);
        var booking1 = new GeneralBooking().setScoutCenter(scoutCenter)
            .setStatus(BookingStatus.OCCUPIED)
            .setExclusiveReservation(true)
            .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
            .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00"));
        var bookingList = List.of(booking1);

        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T10:30:00"),
            LocalDateTime.parse("2025-03-12T11:00:00")
        );

        //when
        var result = BookingIntervalHelper.overlapsWithFullyOccupiedBooking(bookingList, interval);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    void overlapsWithFullyOccupiedBookingReturnsFalseWhenBookingsNotFullyOccupied() {
        //given
        var scoutCenter = newCenter(1);
        var booking1 = new GeneralBooking().setScoutCenter(scoutCenter)
            .setStatus(BookingStatus.OCCUPIED)
            .setExclusiveReservation(false)
            .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
            .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00"));
        var booking2 = new OwnBooking().setScoutCenter(scoutCenter)
            .setStatus(BookingStatus.RESERVED)
            .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
            .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00"));


        var bookingList = List.of(booking1, booking2);

        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T10:30:00"),
            LocalDateTime.parse("2025-03-12T11:00:00")
        );

        //when
        var result = BookingIntervalHelper.overlapsWithFullyOccupiedBooking(bookingList, interval);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void overlapsWithFullyOccupiedBookingReturnsFalseWhenOverlap() {
        //given
        var scoutCenter = newCenter(1);
        var booking1 = new GeneralBooking().setScoutCenter(scoutCenter)
            .setStatus(BookingStatus.OCCUPIED)
            .setExclusiveReservation(true)
            .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
            .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00"));
        var bookingList = List.of(booking1);

        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-04-12T10:30:00"),
            LocalDateTime.parse("2025-04-12T11:00:00")
        );

        //when
        var result = BookingIntervalHelper.overlapsWithFullyOccupiedBooking(bookingList, interval);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void overlapsWithFullyOccupiedBookingReturnsFalseOnAbut() {
        //given
        var scoutCenter = newCenter(1);
        var booking1 = new OwnBooking().setScoutCenter(scoutCenter)
            .setStatus(BookingStatus.OCCUPIED)
            .setExclusiveReservation(true)
            .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
            .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00"));
        var bookingList = List.of(booking1);

        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T11:00:00"),
            LocalDateTime.parse("2025-03-12T12:00:00")
        );

        //when
        var result = BookingIntervalHelper.overlapsWithFullyOccupiedBooking(bookingList, interval);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    void getOverlappingBookingIntervalsAndBookingsAreFromDifferentCentersThrowsError() {
        //given
        var bookingList = List.of(
            new OwnBooking().setScoutCenter(newCenter(1)),
            new GeneralBooking().setScoutCenter(newCenter(2))
        );
        var interval = new Interval(100, 200);

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Bookings are not of same scout center");
    }

    @Test
    void getOverlappingBookingIntervalsWithNoOverlapsShouldReturnEmptyList() {
        //given
        var scoutCenter = newCenter(1);
        var bookingList = List.of(
            new OwnBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(false)
                .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00")),
            new OwnBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(true)
                .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T14:00:00"))
        );


        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-02-12T10:30:00"),
            LocalDateTime.parse("2025-02-12T12:00:00")
        );

        //when
        var result = BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void getOverlappingBookingIntervalsWithAbutsShouldReturnEmptyList() {
        //given
        var scoutCenter = newCenter(1);
        var bookingList = List.of(
            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(false)
                .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00")),
            new OwnBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(true)
                .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T14:00:00"))
        );


        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T09:30:00"),
            LocalDateTime.parse("2025-03-12T10:00:00")
        );

        //when
        var result = BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void getOverlappingBookingIntervalsForOccupied() {
        //given
        var scoutCenter = newCenter(1);
        var booking1 = new GeneralBooking().setScoutCenter(scoutCenter)
            .setStatus(BookingStatus.OCCUPIED)
            .setExclusiveReservation(false)
            .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
            .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00"));
        var bookingList = List.of(booking1);


        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T10:30:00"),
            LocalDateTime.parse("2025-03-12T12:00:00")
        );


        //when
        var result = BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        var expected = List.of(
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:30:00"),
                LocalDateTime.parse("2025-03-12T11:00:00"),
                BookingStatus.OCCUPIED,
                false
            )
        );
        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void getOverlappingBookingIntervalsForFullyOccupied() {
        //given
        var scoutCenter = newCenter(1);
        var booking1 = new OwnBooking().setScoutCenter(scoutCenter)
            .setStatus(BookingStatus.OCCUPIED)
            .setExclusiveReservation(true)
            .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
            .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00"));
        var bookingList = List.of(booking1);


        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T10:30:00"),
            LocalDateTime.parse("2025-03-12T12:00:00")
        );


        //when
        var result = BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        var expected = List.of(
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:30:00"),
                LocalDateTime.parse("2025-03-12T11:00:00"),
                BookingStatus.OCCUPIED,
                true
            )
        );
        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void getOverlappingBookingIntervalsForReserved() {
        //given
        var scoutCenter = newCenter(1);
        var bookingList = List.of(new GeneralBooking().setScoutCenter(scoutCenter)
            .setStatus(BookingStatus.RESERVED)
            .setStartDate(LocalDateTime.parse("2025-03-12T10:00:00"))
            .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00")));


        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T10:30:00"),
            LocalDateTime.parse("2025-03-12T12:00:00")
        );


        //when
        var result = BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        var expected = List.of(
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:30:00"),
                LocalDateTime.parse("2025-03-12T11:00:00"),
                BookingStatus.RESERVED,
                null
            )
        );
        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void getOverlappingBookingIntervalsWithIntervalInStartAndEnd() {
        //given
        var scoutCenter = newCenter(1);
        var bookingList = List.of(
            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.RESERVED)
                .setStartDate(LocalDateTime.parse("2025-03-12T09:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T11:00:00")),
            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.RESERVED)
                .setStartDate(LocalDateTime.parse("2025-03-12T12:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T14:00:00")),
            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(true)
                .setStartDate(LocalDateTime.parse("2025-03-12T13:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T14:00:00"))
        );


        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T10:00:00"),
            LocalDateTime.parse("2025-03-12T13:00:00")
        );


        //when
        var result = BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        var expected = List.of(
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:00:00"),
                LocalDateTime.parse("2025-03-12T11:00:00"),
                BookingStatus.RESERVED,
                null
            ),
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T12:00:00"),
                LocalDateTime.parse("2025-03-12T13:00:00"),
                BookingStatus.RESERVED,
                null
            )
        );
        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void getOverlappingBookingIntervalsWithOverlapsBetweenDifferentStatuses() {
        //given
        var scoutCenter = newCenter(1);
        var bookingList = List.of(
            new OwnBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(true)
                .setStartDate(LocalDateTime.parse("2025-03-12T10:30:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T10:50:00")),
            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(false)
                .setStartDate(LocalDateTime.parse("2025-03-12T09:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T10:30:00")),
            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(false)
                .setStartDate(LocalDateTime.parse("2025-03-12T10:45:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T13:00:00")),
            new OwnBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.RESERVED)
                .setStartDate(LocalDateTime.parse("2025-03-12T08:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T18:00:00"))
        );

        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T10:00:00"),
            LocalDateTime.parse("2025-03-12T14:00:00")
        );

        //when
        var result = BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        var expected = List.of(
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:30:00"),
                LocalDateTime.parse("2025-03-12T10:50:00"),
                BookingStatus.OCCUPIED,
                true
            ),
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:50:00"),
                LocalDateTime.parse("2025-03-12T13:00:00"),
                BookingStatus.OCCUPIED,
                false
            ),
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:00:00"),
                LocalDateTime.parse("2025-03-12T10:30:00"),
                BookingStatus.OCCUPIED,
                false
            ),
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T13:00:00"),
                LocalDateTime.parse("2025-03-12T14:00:00"),
                BookingStatus.RESERVED,
                null
            )
        );
        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void getOverlappingBookingIntervalsWithOverlapsBetweenDifferentAndSameStatuses() {
        //given
        var scoutCenter = newCenter(1);

        var bookingList = List.of(
            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(true)
                .setStartDate(LocalDateTime.parse("2025-03-12T10:30:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T15:00:00")),

            new OwnBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(true)
                .setStartDate(LocalDateTime.parse("2025-03-12T14:30:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T18:00:00")),

            new OwnBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(false)
                .setStartDate(LocalDateTime.parse("2025-03-12T17:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-13T08:00:00")),

            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(false)
                .setStartDate(LocalDateTime.parse("2025-03-12T15:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T18:00:00")),

            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.OCCUPIED)
                .setExclusiveReservation(false)
                .setStartDate(LocalDateTime.parse("2025-03-12T14:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-13T09:30:00")),

            new GeneralBooking().setScoutCenter(scoutCenter)
                .setStatus(BookingStatus.RESERVED)
                .setStartDate(LocalDateTime.parse("2025-03-12T08:00:00"))
                .setEndDate(LocalDateTime.parse("2025-03-12T18:00:00"))
        );

        var interval = IntervalUtils.intervalFromLocalDateTimes(
            LocalDateTime.parse("2025-03-12T10:00:00"),
            LocalDateTime.parse("2025-03-13T18:00:00")
        );


        //when
        var result = BookingIntervalHelper.getOverlappingBookingIntervals(bookingList, interval);

        //then
        var expected = List.of(
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:30:00"),
                LocalDateTime.parse("2025-03-12T18:00:00"),
                BookingStatus.OCCUPIED,
                true
            ),
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T18:00:00"),
                LocalDateTime.parse("2025-03-13T09:30:00"),
                BookingStatus.OCCUPIED,
                false
            ),
            new BookingDateAndStatusDto(
                LocalDateTime.parse("2025-03-12T10:00:00"),
                LocalDateTime.parse("2025-03-12T10:30:00"),
                BookingStatus.RESERVED,
                null
            )
        );
        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }
}