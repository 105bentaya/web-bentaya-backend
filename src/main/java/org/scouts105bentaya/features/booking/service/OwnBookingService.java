package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.dto.in.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.booking.repository.OwnBookingRepository;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OwnBookingService {

    private final BookingRepository bookingRepository;
    private final ScoutCenterRepository scoutCenterRepository;
    private final BookingService bookingService;
    private final OwnBookingRepository ownBookingRepository;

    public OwnBookingService(
        BookingRepository bookingRepository,
        ScoutCenterRepository scoutCenterRepository,
        BookingService bookingService,
        OwnBookingRepository ownBookingRepository) {
        this.bookingRepository = bookingRepository;
        this.scoutCenterRepository = scoutCenterRepository;
        this.bookingService = bookingService;
        this.ownBookingRepository = ownBookingRepository;
    }

    public List<OwnBooking> getAllOwnBookings() {
        return ownBookingRepository.findAll();
    }

    public void addOwnBooking(OwnBookingFormDto dto) {
//        this.saveOwnBooking(dto, new Booking());
    }

    private void saveOwnBooking(OwnBookingFormDto dto, Booking booking) {
//        booking.setStartDate(dto.startDate());
//        booking.setEndDate(dto.endDate());
//        booking.setScoutCenter(scoutCenterRepository.get(dto.scoutCenterId()));
//        booking.setPacks(dto.packs());
//        booking.setObservations(dto.observations());
//        booking.setExclusiveReservation(dto.exclusiveReservation());
//        booking.setOwnBooking(true);
//
//        bookingService.validateBookingDates(booking);
//
//        booking.setStatus(BookingStatus.OCCUPIED);
//        booking.setCreationDate(ZonedDateTime.now());
//
//        bookingRepository.save(booking);
    }
}
