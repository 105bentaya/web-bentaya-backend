package org.scouts105bentaya.dto.booking;

import org.scouts105bentaya.enums.BookingStatus;

import java.time.LocalDateTime;

public class BookingDateDto {

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BookingStatus status;

    private Integer id;

    private Integer packs;

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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPacks() {
        return packs;
    }

    public void setPacks(Integer packs) {
        this.packs = packs;
    }
}
