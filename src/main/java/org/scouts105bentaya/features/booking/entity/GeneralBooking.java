package org.scouts105bentaya.features.booking.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.user.User;

import java.util.List;

@Getter
@Setter
@Entity
@Accessors(chain = true)
public class GeneralBooking extends Booking {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    private String organizationName;
    @Column(nullable = false)
    private String cif;
    @Column(length = 2000)
    private String facilityUse;
    @Column(length = 511)
    private String groupDescription;
    private String contactName;
    private String contactRelationship;
    private String contactMail;
    private String contactPhone;
    private Float price;
    private boolean userConfirmedDocuments;
    private boolean finished;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    private List<BookingDocument> bookingDocumentList;

    @OneToOne(cascade = CascadeType.PERSIST)
    private BookingDocumentFile incidencesFile;
}
