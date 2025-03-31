package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.dto.in.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Slf4j
@Service
public class OwnBookingService {

    private final BookingRepository bookingRepository;
    private final ScoutCenterRepository scoutCenterRepository;
    private final BookingService bookingService;

    public OwnBookingService(
        BookingRepository bookingRepository,
        ScoutCenterRepository scoutCenterRepository,
        BookingService bookingService
    ) {
        this.bookingRepository = bookingRepository;
        this.scoutCenterRepository = scoutCenterRepository;
        this.bookingService = bookingService;
    }

    public void addOwnBooking(OwnBookingFormDto dto) {
        this.saveOwnBooking(dto, new Booking());
    }

    public void updateOwnBooking(OwnBookingFormDto dto, Integer id) {//todo revisar
        Booking booking = this.bookingRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        if (!booking.isOwnBooking()) {
            log.warn("updateOwnBooking - booking is not own booking");
            throw new WebBentayaBadRequestException("No se puede editar una reserva ajena");
        }
        this.saveOwnBooking(dto, booking);
    }

    private void saveOwnBooking(OwnBookingFormDto dto, Booking booking) {
        booking.setStartDate(dto.startDate());
        booking.setEndDate(dto.endDate());
        booking.setScoutCenter(scoutCenterRepository.get(dto.scoutCenterId()));
        booking.setPacks(dto.packs());
        booking.setObservations(dto.observations());
        booking.setExclusiveReservation(dto.exclusiveReservation());
        booking.setOwnBooking(true);

        bookingService.validateBookingDates(booking);

        booking.setStatus(BookingStatus.OCCUPIED);
        booking.setCreationDate(ZonedDateTime.now());

        bookingRepository.save(booking);
    }

    public Booking cancelOwnBooking(Integer id, String reason) {
        Booking booking = this.bookingRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        if (!booking.isOwnBooking()) {
            log.warn("cancelOwnBooking - booking is not own booking");
            throw new WebBentayaBadRequestException("No se puede cancelar una reserva ajena");
        }
        if (booking.getStatus() == BookingStatus.CANCELED) {
            log.warn("cancelOwnBooking - booking is already canceled");
            throw new WebBentayaBadRequestException("No se puede cancelar una reserva cancelada");
        }
        booking.setStatus(BookingStatus.CANCELED);
        booking.setStatusObservations(reason);
        return bookingRepository.save(booking);
    }

}
