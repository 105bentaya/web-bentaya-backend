package org.scouts105bentaya.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.enums.BookingStatus;
import org.scouts105bentaya.enums.ScoutCenter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    private ScoutCenter scoutCenter;

    private String organizationName;

    private String cif;

    private String facilityUse;

    private int packs;

    private String contactName;

    private String contactRelationship;

    private String contactMail;

    private String contactPhone;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String observations;

    private String statusObservations;

    private Float price;

    private boolean exclusiveReservation;

    private boolean userConfirmedDocuments;

    private boolean ownBooking;

    private ZonedDateTime creationDate;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    private List<BookingDocument> bookingDocumentList;
}
