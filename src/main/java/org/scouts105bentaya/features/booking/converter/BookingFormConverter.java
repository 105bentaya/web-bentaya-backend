package org.scouts105bentaya.features.booking.converter;

import org.scouts105bentaya.features.booking.dto.in.BookingFormDto;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import static java.time.temporal.ChronoUnit.MINUTES;

@Component
public class BookingFormConverter extends GenericConverter<GeneralBooking, BookingFormDto> {
    private final ScoutCenterRepository scoutCenterRepository;

    public BookingFormConverter(ScoutCenterRepository scoutCenterRepository) {
        super();
        this.scoutCenterRepository = scoutCenterRepository;
    }

    @Override
    public GeneralBooking convertFromDto(BookingFormDto dto) {
        GeneralBooking entity = new GeneralBooking();
        entity.setOrganizationName(dto.groupName());
        entity.setCif(dto.cif().toUpperCase());
        entity.setFacilityUse(dto.facilityUse());
        entity.setGroupDescription(dto.groupDescription());
        entity.setContactName(dto.contactName());
        entity.setContactRelationship(dto.relationship());
        entity.setContactMail(dto.email());
        entity.setContactPhone(dto.phone());
        entity.setPacks(dto.packs());
        entity.setScoutCenter(scoutCenterRepository.get(dto.scoutCenterId()));
        entity.setStartDate(dto.startDate().truncatedTo(MINUTES));
        entity.setEndDate(dto.endDate().truncatedTo(MINUTES));
        entity.setObservations(dto.observations());
        entity.setExclusiveReservation(dto.exclusiveReservation());
        return entity;
    }

    @Override
    public BookingFormDto convertFromEntity(GeneralBooking entity) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }
}
