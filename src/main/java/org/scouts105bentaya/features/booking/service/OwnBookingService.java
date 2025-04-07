package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.dto.in.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.booking.repository.OwnBookingRepository;
import org.scouts105bentaya.features.booking.specification.BookingSpecification;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.features.booking.specification.OwnBookingSpecification;
import org.scouts105bentaya.features.group.GroupRepository;
import org.scouts105bentaya.features.group.GroupService;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
public class OwnBookingService {

    private final BookingRepository bookingRepository;
    private final ScoutCenterRepository scoutCenterRepository;
    private final BookingService bookingService;
    private final OwnBookingRepository ownBookingRepository;
    private final GroupService groupService;

    public OwnBookingService(
        BookingRepository bookingRepository,
        ScoutCenterRepository scoutCenterRepository,
        BookingService bookingService,
        OwnBookingRepository ownBookingRepository,
        GroupService groupService
    ) {
        this.bookingRepository = bookingRepository;
        this.scoutCenterRepository = scoutCenterRepository;
        this.bookingService = bookingService;
        this.ownBookingRepository = ownBookingRepository;
        this.groupService = groupService;
    }

    public Page<OwnBooking> findAll(BookingSpecificationFilter filter) {
        return ownBookingRepository.findAll(new OwnBookingSpecification(filter), filter.getPageable());
    }

    public void addOwnBooking(OwnBookingFormDto dto) {
        OwnBooking booking = new OwnBooking();
        booking.setPacks(dto.packs());
        booking.setStartDate(dto.startDate());
        booking.setEndDate(dto.endDate());
        booking.setScoutCenter(scoutCenterRepository.get(dto.scoutCenterId()));
        booking.setPacks(dto.packs());
        booking.setObservations(dto.observations());
        booking.setExclusiveReservation(booking.getScoutCenter().isAlwaysExclusive() || dto.exclusiveReservation());
        booking.setGroup(dto.groupId() == 0 ? null : groupService.findById(dto.groupId()));

        bookingService.validateBookingDates(booking);

        booking.setStatus(BookingStatus.RESERVED);
        booking.setCreationDate(ZonedDateTime.now());

        bookingRepository.save(booking);
    }
}
