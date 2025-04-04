package org.scouts105bentaya.features.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.converter.BookingConverter;
import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.data.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.data.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.dto.data.PendingBookingsDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDateFormDto;
import org.scouts105bentaya.features.booking.dto.in.BookingFormDto;
import org.scouts105bentaya.features.booking.dto.in.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.booking.service.BookingService;
import org.scouts105bentaya.features.booking.service.OwnBookingService;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/booking")
public class BookingController {

    private final BookingService bookingService;
    private final BookingConverter bookingConverter;
    private final BookingRepository bookingRepository;
    private final OwnBookingService ownBookingService;

    public BookingController(
        BookingService bookingService,
        BookingConverter bookingConverter,
        BookingRepository bookingRepository,
        OwnBookingService ownBookingService
    ) {
        this.bookingService = bookingService;
        this.bookingConverter = bookingConverter;
        this.bookingRepository = bookingRepository;
        this.ownBookingService = ownBookingService;
    }

    //MANAGER

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping
    public PageDto<BookingDto> getAll(BookingSpecificationFilter filterDto) {
        log.info("METHOD BookingController.getAll --- {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertEntityPageToPageDto(bookingService.findAll(filterDto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/pending")
    public PendingBookingsDto getAllPending(BookingSpecificationFilter filterDto) {
        log.info("METHOD BookingController.getAllPending --- {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingService.findAllPending(filterDto);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/dates")
    public List<BookingCalendarInfoDto> getBookingForCalendar(BookingSpecificationFilter filterDto) {
        log.info("METHOD BookingController.getBookingForCalendar{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingService.getBookingDates(filterDto);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Integer id) {
        log.info("METHOD BookingController.getById{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingRepository.get(id));
    }

    //OWN

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/own/new")
    public void addOwnBooking(@RequestBody @Valid OwnBookingFormDto formDto) {
        log.info("METHOD BookingController.addOwnBooking{}", SecurityUtils.getLoggedUserUsernameForLog());
        ownBookingService.addOwnBooking(formDto);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PutMapping("/own/update/{id}")
    public void updateOwnBooking(@RequestBody @Valid OwnBookingFormDto formDto, @PathVariable Integer id) {
        log.info("METHOD BookingController.updatedOwnBooking --- PARAMS: id{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        ownBookingService.updateOwnBooking(formDto, id);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @DeleteMapping("/own/cancel/{id}")
    public BookingDto cancelOwnBooking(@PathVariable Integer id, @RequestParam String reason) {
        log.info("METHOD BookingController.cancelOwnBooking --- PARAMS: id{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(ownBookingService.cancelOwnBooking(id, reason));
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
        log.info("METHOD BookingController.saveBookingForm");
        this.bookingService.saveFromForm(bookingFormDto);
    }
}
