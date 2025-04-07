package org.scouts105bentaya.features.booking.dto.data;

import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.scout_center.dto.BasicScoutCenterDto;

import java.time.LocalDateTime;

public record BookingInfoDto(
    int id,
    boolean ownBooking,
    BookingStatus status,
    BasicScoutCenterDto scoutCenter,
    LocalDateTime startDate,
    LocalDateTime endDate,
    int packs,
    String cif,
    String organizationName
) {
    public static BookingInfoDto fromEntity(Booking entity) {
        return new BookingInfoDto(
            entity.getId(),
            entity instanceof OwnBooking,
            entity.getStatus(),
            BasicScoutCenterDto.of(entity.getScoutCenter()),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getPacks(),
            entity instanceof GeneralBooking generalBooking ? generalBooking.getCif() : null,
            entity instanceof GeneralBooking generalBooking ? generalBooking.getOrganizationName() : null
        );
    }
}
