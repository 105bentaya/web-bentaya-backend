package org.scouts105bentaya.features.booking.dto;

import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.scout_center.dto.BasicScoutCenterDto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record OwnBookingDto(
    Integer id,
    BookingStatus status,
    BasicScoutCenterDto scoutCenter,
    GroupBasicDataDto group,
    int packs,
    boolean exclusiveReservation,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String observations,
    String statusObservations,
    ZonedDateTime creationDate,
    boolean isOwnBooking
) implements BookingDto {
    public static OwnBookingDto fromEntity(OwnBooking entity) {
        return new OwnBookingDto(
            entity.getId(),
            entity.getStatus(),
            BasicScoutCenterDto.of(entity.getScoutCenter()),
            GroupBasicDataDto.fromGroupNullAsGeneral(entity.getGroup()),
            entity.getPacks(),
            entity.isExclusiveReservation(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getObservations(),
            entity.getStatusObservations(),
            entity.getCreationDate(),
            true
        );
    }
}
