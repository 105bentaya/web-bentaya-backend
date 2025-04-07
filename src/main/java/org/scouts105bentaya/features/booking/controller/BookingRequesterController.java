package org.scouts105bentaya.features.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.converter.BookingConverter;
import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.data.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.data.BookingInfoDto;
import org.scouts105bentaya.features.booking.dto.data.PendingBookingsDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.repository.GeneralBookingRepository;
import org.scouts105bentaya.features.booking.service.BookingService;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/booking/requester")
@PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER')")
public class BookingRequesterController {

    private final BookingService bookingService;
    private final BookingConverter bookingConverter;
    private final AuthService authService;
    private final GeneralBookingRepository generalBookingRepository;

    public BookingRequesterController(
        BookingService bookingService,
        BookingConverter bookingConverter,
        AuthService authService,
        GeneralBookingRepository generalBookingRepository
    ) {
        this.bookingService = bookingService;
        this.bookingConverter = bookingConverter;
        this.authService = authService;
        this.generalBookingRepository = generalBookingRepository;
    }

    @GetMapping
    public PageDto<BookingInfoDto> getAll(BookingSpecificationFilter filterDto) {
        log.info("getAll - {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        filterDto.setUserId(authService.getLoggedUser().getId());
        Page<Booking> page = bookingService.findAll(filterDto);
        return new PageDto<>(page.getTotalElements(), page.stream().map(BookingInfoDto::fromEntity).toList()) ;
    }

    @GetMapping("/pending")
    public PendingBookingsDto getAllPending(BookingSpecificationFilter filterDto) {
        log.info("getAllPending - {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        filterDto.setUserId(authService.getLoggedUser().getId());
        return bookingService.findAllPending(filterDto);
    }

    @GetMapping("/dates")
    public List<BookingCalendarInfoDto> getBookingForCalendar(BookingSpecificationFilter filterDto) {
        log.info("getBookingForCalendar{}", SecurityUtils.getLoggedUserUsernameForLog());
        filterDto.setUserId(authService.getLoggedUser().getId());
        return bookingService.getBookingDates(filterDto);
    }

    @GetMapping("/latest")
    public BookingDto getLatestUserBookings() { //todo redo
        log.info("getLatestUserBookings{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingService.findLatestByCurrentUser());
    }

    @PreAuthorize("@authLogic.userOwnsBooking(#id)")
    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Integer id) {
        log.info("getById - id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(generalBookingRepository.get(id));
    }
}
