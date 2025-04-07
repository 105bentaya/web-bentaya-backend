package org.scouts105bentaya.features.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.converter.BookingConverter;
import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.OwnBookingDto;
import org.scouts105bentaya.features.booking.dto.data.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.data.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.dto.data.BookingInfoDto;
import org.scouts105bentaya.features.booking.dto.data.PendingBookingsDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDateFormDto;
import org.scouts105bentaya.features.booking.dto.in.BookingFormDto;
import org.scouts105bentaya.features.booking.dto.in.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.booking.repository.OwnBookingRepository;
import org.scouts105bentaya.features.booking.service.BookingService;
import org.scouts105bentaya.features.booking.service.OwnBookingService;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/booking/own")
public class OwnBookingController {

    private final OwnBookingService ownBookingService;
    private final BookingConverter bookingConverter;
    private final OwnBookingRepository ownBookingRepository;

    public OwnBookingController(
        OwnBookingService ownBookingService,
        BookingConverter bookingConverter, OwnBookingRepository ownBookingRepository) {
        this.ownBookingService = ownBookingService;
        this.bookingConverter = bookingConverter;
        this.ownBookingRepository = ownBookingRepository;
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping()
    public PageDto<OwnBookingDto> getAll(BookingSpecificationFilter filterDto) {
        log.info("getAll - {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        Page<OwnBooking> page = ownBookingService.findAll(filterDto);
        return new PageDto<>(page.getTotalElements(), page.stream().map(OwnBookingDto::fromEntity).toList());
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or (hasAnyRole('SCOUTER', 'GROUP_SCOUTER') and (#formDto.groupId == 0 or @authLogic.scouterHasGroupId(#formDto.groupId)))")
    @PostMapping("/new")
    public void addOwnBooking(@RequestBody @Valid OwnBookingFormDto formDto) {
        log.info("addOwnBooking{}", SecurityUtils.getLoggedUserUsernameForLog());
        ownBookingService.addOwnBooking(formDto);
    }

}
