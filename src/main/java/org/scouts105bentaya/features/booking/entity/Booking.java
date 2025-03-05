package org.scouts105bentaya.features.booking.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
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
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.booking.ScoutCenter;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.user.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    private BookingStatus status;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
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
    @Nullable
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
