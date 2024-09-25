package org.scouts105bentaya.converter.booking;

import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.booking.BookingFormDto;
import org.scouts105bentaya.entity.Booking;
import org.springframework.stereotype.Component;

import static java.time.temporal.ChronoUnit.MINUTES;

@Component
public class BookingFormConverter extends GenericConverter<Booking, BookingFormDto> {
    @Override
    public Booking convertFromDto(BookingFormDto dto) {
        Booking entity = new Booking();
        entity.setOrganizationName(dto.getGroupName());
        entity.setCif(dto.getCif());
        entity.setFacilityUse(dto.getWorkDescription());
        entity.setContactName(dto.getContactName());
        entity.setContactRelationship(dto.getRelationship());
        entity.setContactMail(dto.getEmail());
        entity.setContactPhone(dto.getPhone());
        entity.setPacks(dto.getPacks());
        entity.setScoutCenter(dto.getScoutCenter());
        entity.setStartDate(dto.getStartDate().truncatedTo(MINUTES));
        entity.setEndDate(dto.getEndDate().truncatedTo(MINUTES));
        entity.setObservations(dto.getObservations());
        entity.setExclusiveReservation(dto.isExclusiveReservation());
        return entity;
    }

    @Override
    public BookingFormDto convertFromEntity(Booking entity) {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
