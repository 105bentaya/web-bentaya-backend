package org.scouts105bentaya.converter.booking;

import org.scouts105bentaya.converter.GenericConverter;
import org.scouts105bentaya.dto.booking.BookingDto;
import org.scouts105bentaya.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingConverter extends GenericConverter<Booking, BookingDto> {

    @Override
    public Booking convertFromDto(BookingDto dto) {
        Booking entity = new Booking();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setScoutCenter(dto.getScoutCenter());
        entity.setOrganizationName(dto.getOrganizationName());
        entity.setCif(dto.getCif());
        entity.setFacilityUse(dto.getFacilityUse());
        entity.setPacks(dto.getPacks());
        entity.setContactName(dto.getContactName());
        entity.setContactRelationship(dto.getContactRelationship());
        entity.setContactMail(dto.getContactMail());
        entity.setContactPhone(dto.getContactPhone());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setCreationDate(dto.getCreationDate());
        entity.setObservations(dto.getObservations());
        entity.setExclusiveReservation(dto.isExclusiveReservation());
        entity.setStatusObservations(dto.getStatusObservations());
        entity.setUserConfirmedDocuments(dto.isUserConfirmedDocuments());
        entity.setPrice(dto.getPrice());
        entity.setOwnBooking(dto.isOwnBooking());
        return entity;
    }

    @Override
    public BookingDto convertFromEntity(Booking entity) {
        BookingDto dto = new BookingDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setScoutCenter(entity.getScoutCenter());
        dto.setOrganizationName(entity.getOrganizationName());
        dto.setCif(entity.getCif());
        dto.setFacilityUse(entity.getFacilityUse());
        dto.setPacks(entity.getPacks());
        dto.setContactName(entity.getContactName());
        dto.setContactRelationship(entity.getContactRelationship());
        dto.setContactMail(entity.getContactMail());
        dto.setContactPhone(entity.getContactPhone());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setCreationDate(entity.getCreationDate());
        dto.setObservations(entity.getObservations());
        dto.setExclusiveReservation(entity.isExclusiveReservation());
        dto.setStatusObservations(entity.getStatusObservations());
        dto.setUserConfirmedDocuments(entity.isUserConfirmedDocuments());
        dto.setPrice(entity.getPrice());
        dto.setOwnBooking(entity.isOwnBooking());
        return dto;
    }
}
