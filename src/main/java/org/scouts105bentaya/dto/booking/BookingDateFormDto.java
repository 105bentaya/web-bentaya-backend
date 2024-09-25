package org.scouts105bentaya.dto.booking;

import org.scouts105bentaya.enums.ScoutCenter;

import java.time.LocalDateTime;

public class BookingDateFormDto {

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private ScoutCenter scoutCenter;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public ScoutCenter getScoutCenter() {
        return scoutCenter;
    }

    public void setScoutCenter(ScoutCenter scoutCenter) {
        this.scoutCenter = scoutCenter;
    }
}