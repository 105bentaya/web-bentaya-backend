package org.scouts105bentaya.features.booking.converter;

import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.GeneralBookingDto;
import org.scouts105bentaya.features.booking.dto.OwnBookingDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.scout_center.dto.BasicScoutCenterDto;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class BookingConverter extends GenericConverter<Booking, BookingDto> {

    private final SettingService settingService;

    public BookingConverter(SettingService settingService) {
        super();
        this.settingService = settingService;
    }

    @Override
    public Booking convertFromDto(BookingDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public BookingDto convertFromEntity(Booking entity) {
        if (entity instanceof GeneralBooking generalBooking) {
            return new GeneralBookingDto(
                generalBooking.getId(),
                generalBooking.getStatus(),
                BasicScoutCenterDto.of(generalBooking.getScoutCenter()),
                generalBooking.getOrganizationName(),
                generalBooking.getCif(),
                generalBooking.getFacilityUse(),
                generalBooking.getGroupDescription(),
                generalBooking.getPacks(),
                generalBooking.getContactName(),
                generalBooking.getContactRelationship(),
                generalBooking.getContactMail(),
                generalBooking.getContactPhone(),
                generalBooking.getStartDate(),
                generalBooking.getEndDate(),
                generalBooking.getObservations(),
                generalBooking.getStatusObservations(),
                generalBooking.isExclusiveReservation(),
                generalBooking.getCreationDate(),
                generalBooking.isUserConfirmedDocuments(),
                generalBooking.getPrice(),
                bookingMinutes(generalBooking),
                bookingBillableDays(generalBooking),
                generalBooking.getIncidencesFile() != null
            );
        }
        return OwnBookingDto.fromEntity((OwnBooking) entity);
    }

    private int bookingMinutes(GeneralBooking booking) {
        ZonedDateTime start = ZonedDateTime.of(booking.getStartDate(), GenericConstants.CANARY_ZONE_ID);
        ZonedDateTime end = ZonedDateTime.of(booking.getEndDate(), GenericConstants.CANARY_ZONE_ID);

        return (int) start.until(end, ChronoUnit.MINUTES);
    }

    private int bookingBillableDays(GeneralBooking booking) {
        LocalDateTime billableStart = truncateBookingDates(booking.getStartDate(), SettingEnum.BOOKING_MAX_DAY_NUMBER);
        LocalDateTime billableEnd = truncateBookingDates(booking.getEndDate(), SettingEnum.BOOKING_MIN_DAY_NUMBER);

        return (int) Math.max(1, billableStart.until(billableEnd, ChronoUnit.DAYS));
    }

    private LocalDateTime truncateBookingDates(LocalDateTime date, SettingEnum setting) {
        int maxHour = Integer.parseInt(settingService.findByName(setting).getValue());
        return date.getHour() > maxHour ? date.truncatedTo(ChronoUnit.DAYS).plusDays(1) : date.truncatedTo(ChronoUnit.DAYS);
    }
}
