package org.scouts105bentaya.features.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class BookingDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Booking booking;
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] fileData;
    private String fileUuid;
    private String fileName;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    private BookingDocumentStatus status;
}
