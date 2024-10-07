package org.scouts105bentaya.features.booking.util;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.scouts105bentaya.features.booking.dto.BookingDateFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public final class IntervalUtils {

    private IntervalUtils() {}

    public static Interval intervalFromBooking(Booking booking) {
        return intervalFromLocalDateTimes(booking.getStartDate(), booking.getEndDate());
    }

    public static Interval intervalFromBooking(BookingDateFormDto booking) {
        return intervalFromLocalDateTimes(booking.startDate(), booking.endDate());
    }

    private static Interval intervalFromLocalDateTimes(LocalDateTime startDate, LocalDateTime endDate) {
        Instant instant1 = Instant.ofEpochSecond(startDate.toEpochSecond(ZoneOffset.ofHours(0)));
        Instant instant2 = Instant.ofEpochSecond(endDate.toEpochSecond(ZoneOffset.ofHours(0)));
        return new Interval(instant1, instant2);
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

    public static List<Interval> removeOverlappingSectionsFromSecondIntervalList(List<Interval> master, List<Interval> slave) {
        List<Interval> result = new ArrayList<>();

        for (Interval currentInterval : slave) {
            List<Interval> overlappingIntervals = master.stream()
                    .filter(interval -> interval.overlaps(currentInterval))
                    .toList();

            if (!overlappingIntervals.isEmpty()) {
                Interval newInterval = currentInterval;
                for (Interval interval : overlappingIntervals) {
                    if (currentInterval.getStart().isAfter(interval.getStart()) && currentInterval.getEnd().isBefore(interval.getEnd())) {
                        newInterval = null;
                        break;
                    } else if (currentInterval.getStart().isBefore(interval.getStart()) && currentInterval.getEnd().isBefore(interval.getEnd())) {
                        newInterval = newInterval.withEnd(interval.getStart());
                    } else if (currentInterval.getStart().isAfter(interval.getStart()) && currentInterval.getEnd().isAfter(interval.getEnd())) {
                        newInterval = newInterval.withStart(interval.getEnd());
                    } else if (currentInterval.getStart().isBefore(interval.getStart()) && currentInterval.getEnd().isAfter(interval.getEnd())) {
                        result.add(newInterval.withEnd(interval.getStart()));
                        newInterval = newInterval.withStart(interval.getEnd());
                    }
                }
                if (newInterval != null) {
                    result.add(newInterval);
                }
            } else {
                result.add(currentInterval);
            }
        }
        return result;
    }

    // se puede mejorar bastante, mirar chatgpt
    public static List<Interval> mergeIntervalList(List<Interval> intervals) {
        List<Interval> result = new ArrayList<>();
        for (Interval currentInterval : intervals) {
            List<Interval> overlappingIntervals = result.stream()
                    .filter(interval -> interval.overlaps(currentInterval) || interval.abuts(currentInterval)).toList();
            if (!overlappingIntervals.isEmpty()) {
                result.removeAll(overlappingIntervals);
                Interval mergedInterval = currentInterval;
                for (Interval interval : overlappingIntervals) {
                    mergedInterval = mergeIntervals(mergedInterval, interval);
                }
                result.add(mergedInterval);
            } else {
                result.add(currentInterval);
            }
        }
        return result;
    }

    // operación de unión entre ambos intervalos
    public static Interval mergeIntervals(Interval firstInterval, Interval secondInterval) {
        DateTime start = firstInterval.getStart().isBefore(secondInterval.getStart()) ? firstInterval.getStart() : secondInterval.getStart();
        DateTime end = firstInterval.getEnd().isAfter(secondInterval.getEnd()) ? firstInterval.getEnd() : secondInterval.getEnd();
        return new Interval(start, end);
    }
}
