package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.scout_center.dto.BasicScoutCenterDto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record BookingDto(
    Integer id,
    BookingStatus status,
    BasicScoutCenterDto scoutCenter,
    String organizationName,
    String cif,
    String facilityUse,
    int packs,
    String contactName,
    String contactRelationship,
    String contactMail,
    String contactPhone,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String observations,
    String statusObservations,
    boolean exclusiveReservation,
    ZonedDateTime creationDate,
    boolean userConfirmedDocuments,
    boolean ownBooking,
    Float price,
    int minutes,
    int billableDays,
    boolean hasIncidencesFile
) {
}
