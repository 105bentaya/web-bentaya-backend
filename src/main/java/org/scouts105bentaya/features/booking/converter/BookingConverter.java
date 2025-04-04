package org.scouts105bentaya.features.booking.converter;

import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.scout_center.dto.BasicScoutCenterDto;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
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

    private final ScoutCenterRepository scoutCenterRepository;
    private final SettingService settingService;

    public BookingConverter(ScoutCenterRepository scoutCenterRepository, SettingService settingService) {
        super();
        this.scoutCenterRepository = scoutCenterRepository;
        this.settingService = settingService;
    }

    @Override
    public Booking convertFromDto(BookingDto dto) {
        Booking entity = new Booking();
        entity.setId(dto.id());
        entity.setStatus(dto.status());
        entity.setScoutCenter(scoutCenterRepository.get(dto.id()));
        entity.setOrganizationName(dto.organizationName());
        entity.setCif(dto.cif());
        entity.setFacilityUse(dto.facilityUse());
        entity.setPacks(dto.packs());
        entity.setContactName(dto.contactName());
        entity.setContactRelationship(dto.contactRelationship());
        entity.setContactMail(dto.contactMail());
        entity.setContactPhone(dto.contactPhone());
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
        entity.setCreationDate(dto.creationDate());
        entity.setObservations(dto.observations());
        entity.setExclusiveReservation(dto.exclusiveReservation());
        entity.setStatusObservations(dto.statusObservations());
        entity.setUserConfirmedDocuments(dto.userConfirmedDocuments());
        entity.setPrice(dto.price());
        entity.setOwnBooking(dto.ownBooking());
        return entity;
    }

    @Override
    public BookingDto convertFromEntity(Booking entity) {
        return new BookingDto(
            entity.getId(),
            entity.getStatus(),
            BasicScoutCenterDto.of(entity.getScoutCenter()),
            entity.getOrganizationName(),
            entity.getCif(),
            entity.getFacilityUse(),
            entity.getPacks(),
            entity.getContactName(),
            entity.getContactRelationship(),
            entity.getContactMail(),
            entity.getContactPhone(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getObservations(),
            entity.getStatusObservations(),
            entity.isExclusiveReservation(),
            entity.getCreationDate(),
            entity.isUserConfirmedDocuments(),
            entity.isOwnBooking(),
            entity.getPrice(),
            bookingMinutes(entity),
            bookingBillableDays(entity),
            entity.getIncidencesFile() != null
        );
    }

    private int bookingMinutes(Booking booking) {
        ZonedDateTime start = ZonedDateTime.of(booking.getStartDate(), GenericConstants.CANARY_ZONE_ID);
        ZonedDateTime end = ZonedDateTime.of(booking.getEndDate(), GenericConstants.CANARY_ZONE_ID);

        return (int) start.until(end, ChronoUnit.MINUTES);
    }

    private int bookingBillableDays(Booking booking) {
        LocalDateTime billableStart = truncateBookingDates(booking.getStartDate(), SettingEnum.BOOKING_MAX_DAY_NUMBER);
        LocalDateTime billableEnd = truncateBookingDates(booking.getEndDate(), SettingEnum.BOOKING_MIN_DAY_NUMBER);

        return (int) Math.max(1, billableStart.until(billableEnd, ChronoUnit.DAYS));
    }

    private LocalDateTime truncateBookingDates(LocalDateTime date, SettingEnum setting) {
        int maxHour = Integer.parseInt(settingService.findByName(setting).getValue());
        return date.getHour() > maxHour ? date.truncatedTo(ChronoUnit.DAYS).plusDays(1) : date.truncatedTo(ChronoUnit.DAYS);
    }
}
