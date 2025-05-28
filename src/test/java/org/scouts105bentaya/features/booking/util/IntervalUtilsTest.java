package org.scouts105bentaya.features.booking.util;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.jupiter.api.Test;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

class IntervalUtilsTest {

    @Test
    void intervalFromBookingIsSameTimeAsBooking() {
        //given
        var start = LocalDateTime.of(2020, 1, 1, 0, 0);
        var end = LocalDateTime.of(2020, 1, 4, 1, 0);
        Booking booking = new GeneralBooking()
            .setStartDate(start)
            .setEndDate(end);

        //when
        var result = IntervalUtils.intervalFromBooking(booking);

        //then
        Assertions.assertThat(result.getStart().toInstant().getMillis()).isEqualTo(start.toEpochSecond(ZoneOffset.ofTotalSeconds(0)) * 1000);
        Assertions.assertThat(result.getEnd().toInstant().getMillis()).isEqualTo(end.toEpochSecond(ZoneOffset.ofTotalSeconds(0)) * 1000);
    }

    @Test
    void intervalFromBookingWithWrongTimesThrowsException() {
        //given
        var start = LocalDateTime.of(2020, 1, 4, 1, 0);
        var end = LocalDateTime.of(2020, 1, 1, 0, 0);
        Booking booking = new GeneralBooking()
            .setStartDate(start)
            .setEndDate(end);

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> IntervalUtils.intervalFromBooking(booking);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("The end instant must be greater than the start instant");
    }

    @Test
    void jodaLocalDateTimeToJavaDateTime() {
        //given
        DateTime dateTime = new DateTime(2020, 1, 1, 0, 0);

        //when
        LocalDateTime result = IntervalUtils.jodaLocalDateTimeToJavaDateTime(dateTime);

        //then
        Assertions.assertThat(result.getYear()).isEqualTo(2020);
        Assertions.assertThat(result.getMonth()).isEqualTo(Month.of(1));
        Assertions.assertThat(result.getDayOfMonth()).isEqualTo(1);
        Assertions.assertThat(result.getHour()).isZero();
        Assertions.assertThat(result.getMinute()).isZero();
    }


    @Test
    void mergeIntervalsShouldReturnMergedInterval() {
        //given
        Interval interval1 = new Interval(13, 20);
        Interval interval2 = new Interval(15, 22);

        //when
        var result = IntervalUtils.mergeIntervals(interval1, interval2);

        //then
        Assertions.assertThat(result).isEqualTo(new Interval(13, 22));
    }

    @Test
    void mergeIntervalsShouldReturnMergedInterval2() {
        //given
        Interval interval1 = new Interval(15, 22);
        Interval interval2 = new Interval(13, 20);

        //when
        var result = IntervalUtils.mergeIntervals(interval1, interval2);

        //then
        Assertions.assertThat(result).isEqualTo(new Interval(13, 22));
    }

    @Test
    void mergeIntervalsThatAbutShouldReturnMergedInterval() {
        //given
        Interval interval1 = new Interval(13, 20);
        Interval interval2 = new Interval(20, 22);

        //when
        var result = IntervalUtils.mergeIntervals(interval1, interval2);

        //then
        Assertions.assertThat(result).isEqualTo(new Interval(13, 22));
    }

    @Test
    void mergeIntervalsThatDontOverlapShouldThrowError() {
        //given
        Interval interval1 = new Interval(13, 20);
        Interval interval2 = new Interval(21, 22);

        //when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> IntervalUtils.mergeIntervals(interval1, interval2);

        //then
        Assertions.assertThatThrownBy(throwingCallable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageStartingWith("Intervals do not overlap");
    }

    @Test
    void mergeIntervalList() {
        //given
        List<Interval> intervals = List.of(
            new Interval(10, 20),
            new Interval(15, 25),
            new Interval(11, 23),
            new Interval(15, 25),
            new Interval(40, 56),
            new Interval(26, 29),
            new Interval(7, 12),
            new Interval(37, 38),
            new Interval(38, 39)
        );

        //when
        var result = IntervalUtils.mergeIntervalList(intervals);

        //then
        var expected = List.of(
            new Interval(7, 25),
            new Interval(26, 29),
            new Interval(37, 39),
            new Interval(40, 56)
        );

        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void removeIntervalListOverlappingSectionsWithIntervalList() {
        //given
        List<Interval> mainIntervals = List.of(
            new Interval(60, 75),
            new Interval(10, 20),
            new Interval(30, 50),
            new Interval(150, 160),
            new Interval(100, 120)
        );

        List<Interval> intervalsToCheck = List.of(
            new Interval(18, 35),
            new Interval(58, 60),
            new Interval(99, 105),
            new Interval(5, 15),
            new Interval(85, 90),
            new Interval(52, 56),
            new Interval(150, 155),
            new Interval(65, 70),
            new Interval(75, 80)
        );

        //when
        var result = IntervalUtils.removeIntervalListOverlappingSectionsWithIntervalList(mainIntervals, intervalsToCheck);

        //then
        var expected = List.of(
            new Interval(5, 10),
            new Interval(20, 30),
            new Interval(52, 56),
            new Interval(58, 60),
            new Interval(75, 80),
            new Interval(85, 90),
            new Interval(99, 100)
        );

        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void removeIntervalsFullyOverlappingSectionsWithIntervalListWithMainIntervalListNotMerged() {
        //given
        List<Interval> mainIntervals = List.of(
            new Interval(10, 20),
            new Interval(11, 20),
            new Interval(15, 25),
            new Interval(30, 40)
        );

        List<Interval> intervalsToCheck = List.of(
            new Interval(5, 15),
            new Interval(11, 13),
            new Interval(14, 35),
            new Interval(22, 35),
            new Interval(30, 45)
        );

        //when
        var result = IntervalUtils.removeIntervalListOverlappingSectionsWithIntervalList(mainIntervals, intervalsToCheck);

        //then
        var expected = List.of(
            new Interval(5, 10),
            new Interval(25, 30),
            new Interval(25, 30),
            new Interval(40, 45)
        );

        Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }
}