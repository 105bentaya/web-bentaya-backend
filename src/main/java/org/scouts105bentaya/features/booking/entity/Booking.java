package org.scouts105bentaya.features.booking.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.scout_center.entity.ScoutCenter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    private BookingStatus status;
    @ManyToOne
    private ScoutCenter scoutCenter;
    private int packs;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Nullable
    @Column(length = 1023)
    private String observations;
    @Column(length = 2047)
    private String statusObservations;
    private boolean exclusiveReservation;
    private ZonedDateTime creationDate;
}
