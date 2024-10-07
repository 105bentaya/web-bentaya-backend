package org.scouts105bentaya.features.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.booking.ScoutCenter;
import org.scouts105bentaya.features.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record BookingDto(
    Integer id,
    @NotNull BookingStatus status,
    @NotNull ScoutCenter scoutCenter,
    @Length(max = 255) @NotBlank String organizationName,
    @Length(max = 255) @NotBlank String cif,
    @Length(max = 511) @NotBlank String facilityUse,
    @Positive @NotNull int packs,
    @Length(max = 255) @NotBlank String contactName,
    @Length(max = 255) @NotBlank String contactRelationship,
    @Length(max = 255) @NotBlank @Email String contactMail,
    @Length(max = 255) @NotBlank String contactPhone,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    @Length(max = 1023) String observations,
    String statusObservations,
    boolean exclusiveReservation,
    ZonedDateTime creationDate,
    boolean userConfirmedDocuments,
    boolean ownBooking,
    Float price
) {
}
