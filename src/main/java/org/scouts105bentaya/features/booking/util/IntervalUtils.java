package org.scouts105bentaya.features.booking.util;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.base.AbstractInterval;
import org.scouts105bentaya.features.booking.dto.in.BookingDateFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class IntervalUtils {

    private IntervalUtils() {
    }

    public static Interval intervalFromBooking(Booking booking) {
        return intervalFromLocalDateTimes(booking.getStartDate(), booking.getEndDate());
    }

    public static Interval intervalFromBooking(BookingDateFormDto booking) {
        return intervalFromLocalDateTimes(booking.startDate(), booking.endDate());
    }

    public static Interval intervalFromLocalDateTimes(LocalDateTime startDate, LocalDateTime endDate) {
        Instant instant1 = Instant.ofEpochSecond(startDate.toEpochSecond(ZoneOffset.ofHours(0)));
        Instant instant2 = Instant.ofEpochSecond(endDate.toEpochSecond(ZoneOffset.ofHours(0)));
        return new Interval(instant1, instant2);
    }

    public static Interval intervalFromLocalDates(LocalDate startDate, LocalDate endDate) {
        Instant instant1 = Instant.ofEpochSecond(startDate.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.ofHours(0)));
        Instant instant2 = Instant.ofEpochSecond(endDate.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.ofHours(0)));
        return new Interval(instant1, instant2);
    }

    public static boolean intervalsOverlapOrAbut(List<Interval> intervals) {
        List<Interval> sortedIntervals = intervals.stream()
            .sorted(Comparator.comparing(Interval::getStart))
            .toList();

        for (int i = 0; i < sortedIntervals.size() - 1; i++) {
            Interval currentInterval = sortedIntervals.get(i);
            Interval nextInterval = sortedIntervals.get(i + 1);
            if (currentInterval.overlaps(nextInterval) || currentInterval.abuts(nextInterval)) {
                return true;
            }
        }
        return false;
    }

    public static LocalDateTime jodaLocalDateTimeToJavaDateTime(DateTime dateTime) {
        return LocalDateTime.of(
            dateTime.getYear(),
            dateTime.getMonthOfYear(),
            dateTime.getDayOfMonth(),
            dateTime.getHourOfDay(),
            dateTime.getMinuteOfHour()
        );
    }

    public static List<Interval> mergeIntervalList(List<Interval> intervals) {
        intervals = new ArrayList<>(intervals);
        intervals.sort(Comparator.comparing(AbstractInterval::getStart));

        List<Interval> result = new ArrayList<>();
        for (Interval currentInterval : intervals) {
            int last = result.size() - 1;
            if (result.isEmpty() || !(result.get(last).overlaps(currentInterval) || result.get(last).abuts(currentInterval))) {
                result.add(currentInterval);
            } else {
                result.set(last, mergeIntervals(result.get(last), currentInterval));
            }
        }

        return result;
    }

    public static Interval mergeIntervals(Interval firstInterval, Interval secondInterval) {
        if (!firstInterval.overlaps(secondInterval) && !firstInterval.abuts(secondInterval))
            throw new IllegalArgumentException("Intervals do not overlap");
        DateTime start = firstInterval.getStart().isAfter(secondInterval.getStart()) ? secondInterval.getStart() : firstInterval.getStart();
        DateTime end = firstInterval.getEnd().isBefore(secondInterval.getEnd()) ? secondInterval.getEnd() : firstInterval.getEnd();
        return new Interval(start, end);
    }

    public static List<Interval> removeIntervalListOverlappingSectionsWithIntervalList(List<Interval> mainIntervals, List<Interval> intervalsToCheck) {
        return intervalsToCheck.stream()
            .flatMap(intervalToCheck -> removeIntervalOverlappingSectionsWithIntervalList(mergeIntervalList(mainIntervals), intervalToCheck).stream())
            .toList();
    }

    private static List<Interval> removeIntervalOverlappingSectionsWithIntervalList(List<Interval> mainIntervals, Interval intervalToCheck) {
        List<Interval> intervalToCheckRemaining = new ArrayList<>();
        intervalToCheckRemaining.add(intervalToCheck);
        mainIntervals = mainIntervals.stream().filter(mainInterval -> mainInterval.overlaps(intervalToCheck)).toList();

        for (Interval mainInterval : mainIntervals) {
            List<Interval> newParts = new ArrayList<>();
            for (Interval part : intervalToCheckRemaining) {
                newParts.addAll(removeIntervalOverlappingSectionsWithInterval(mainInterval, part));
            }
            intervalToCheckRemaining = newParts;
        }
        return intervalToCheckRemaining;
    }

    private static List<Interval> removeIntervalOverlappingSectionsWithInterval(Interval mainInterval, Interval intervalToCheck) {
        List<Interval> result = new ArrayList<>();
        if (intervalToCheck.overlaps(mainInterval)) {
            if (intervalToCheck.getStart().isBefore(mainInterval.getStart())) {
                result.add(new Interval(intervalToCheck.getStart(), mainInterval.getStart()));
            }
            if (intervalToCheck.getEnd().isAfter(mainInterval.getEnd())) {
                result.add(new Interval(mainInterval.getEnd(), intervalToCheck.getEnd()));
            }
        } else {
            result.add(mainInterval);
        }
        return result;
    }
}
