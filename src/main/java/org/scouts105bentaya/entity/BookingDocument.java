package org.scouts105bentaya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.scouts105bentaya.enums.BookingDocumentStatus;

@Entity
public class BookingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Booking booking;

    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] fileData;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private BookingDocumentStatus status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
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
