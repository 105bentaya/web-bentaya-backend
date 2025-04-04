package org.scouts105bentaya.features.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.converter.BookingConverter;
import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.data.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.data.PendingBookingsDto;
import org.scouts105bentaya.features.booking.service.BookingService;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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

    public BookingRequesterController(
        BookingService bookingService,
        BookingConverter bookingConverter,
        AuthService authService
    ) {
        this.bookingService = bookingService;
        this.bookingConverter = bookingConverter;
        this.authService = authService;
    }

    @GetMapping
    public PageDto<BookingDto> getAll(BookingSpecificationFilter filterDto) {
        log.info("METHOD BookingRequesterController.getAll --- {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        filterDto.setUserId(authService.getLoggedUser().getId());
        return bookingConverter.convertEntityPageToPageDto(bookingService.findAll(filterDto));
    }

    @GetMapping("/pending")
    public PendingBookingsDto getAllPending(BookingSpecificationFilter filterDto) {
        log.info("METHOD BookingRequesterController.getAllPending --- {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        filterDto.setUserId(authService.getLoggedUser().getId());
        return bookingService.findAllPending(filterDto);
    }

    @GetMapping("/dates")
    public List<BookingCalendarInfoDto> getBookingForCalendar(BookingSpecificationFilter filterDto) {
        log.info("METHOD BookingRequesterController.getBookingForCalendar{}", SecurityUtils.getLoggedUserUsernameForLog());
        filterDto.setUserId(authService.getLoggedUser().getId());
        return bookingService.getBookingDates(filterDto);
    }

    @GetMapping("/latest")
    public BookingDto getLatestUserBookings() {
        log.info("METHOD BookingRequesterController.getLatestUserBookings{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingService.findLatestByCurrentUser());
    }
}
