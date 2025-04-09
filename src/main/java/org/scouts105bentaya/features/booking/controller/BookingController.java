package org.scouts105bentaya.features.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.converter.BookingConverter;
import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.data.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.data.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.dto.data.BookingInfoDto;
import org.scouts105bentaya.features.booking.dto.data.PendingBookingsDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDateFormDto;
import org.scouts105bentaya.features.booking.dto.in.BookingFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.booking.service.BookingService;
import org.scouts105bentaya.features.booking.service.GeneralBookingService;
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

@Slf4j
@RestController
@RequestMapping("api/booking")
public class BookingController {

    private final BookingService bookingService;
    private final BookingConverter bookingConverter;
    private final BookingRepository bookingRepository;
    private final GeneralBookingService generalBookingService;

    public BookingController(
        BookingService bookingService,
        BookingConverter bookingConverter,
        BookingRepository bookingRepository,
        GeneralBookingService generalBookingService
    ) {
        this.bookingService = bookingService;
        this.bookingConverter = bookingConverter;
        this.bookingRepository = bookingRepository;
        this.generalBookingService = generalBookingService;
    }

    //MANAGER

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping
    public PageDto<BookingInfoDto> getAll(BookingSpecificationFilter filterDto) {
        log.info("getAll - {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        Page<Booking> page = bookingService.findAll(filterDto);
        return new PageDto<>(page.getTotalElements(), page.stream().map(BookingInfoDto::fromEntity).toList());
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/pending")
    public PendingBookingsDto getAllPending(BookingSpecificationFilter filterDto) {
        log.info("getAllPending - {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingService.findAllPending(filterDto);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/dates")
    public List<BookingCalendarInfoDto> getBookingForCalendar(BookingSpecificationFilter filterDto) {
        log.info("getBookingForCalendar{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingService.getBookingDates(filterDto);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Integer id) {
        log.info("getById{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingRepository.get(id));
    }

    //PUBLIC

    @GetMapping("/public/{centerId}")
    public List<BookingDateAndStatusDto> getBasicBookingStatusesByCenter(@PathVariable Integer centerId) {
        return bookingService.getReservationDates(centerId);
    }

    @PostMapping("/public/check-booking")
    public List<BookingDateAndStatusDto> getIntervalBookingStatusesByCenter(@RequestBody BookingDateFormDto dto) {
        return this.bookingService.getScoutCenterBookingDatesStatuses(dto);
    }

    @PostMapping("/public/form")
    public void saveBookingForm(@RequestBody @Valid BookingFormDto bookingFormDto) {
        log.info("saveBookingForm");
        generalBookingService.saveFromForm(bookingFormDto);
    }
}
