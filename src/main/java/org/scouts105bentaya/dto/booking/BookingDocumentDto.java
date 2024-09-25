package org.scouts105bentaya.dto.booking;

import org.scouts105bentaya.enums.BookingDocumentStatus;

public class BookingDocumentDto {

    private Integer id;

    private Integer bookingId;

    private String fileName;

    private BookingDocumentStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BookingDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(BookingDocumentStatus status) {
        this.status = status;
    }
}
