package org.scouts105bentaya.features.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.converter.BookingConverter;
import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.in.BookingAcceptedDto;
import org.scouts105bentaya.features.booking.dto.in.BookingConfirmedDto;
import org.scouts105bentaya.features.booking.dto.in.BookingStatusUpdateDto;
import org.scouts105bentaya.features.booking.dto.in.BookingWarningDto;
import org.scouts105bentaya.features.booking.service.BookingStatusService;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/booking-status")
public class BookingStatusController {

    private final BookingConverter bookingConverter;
    private final BookingStatusService bookingStatusService;

    public BookingStatusController(
        BookingConverter bookingConverter,
        BookingStatusService bookingStatusService
    ) {
        this.bookingConverter = bookingConverter;
        this.bookingStatusService = bookingStatusService;
    }

    //MANAGER

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PatchMapping("accept/{bookingId}")
    public BookingDto acceptBooking(@PathVariable Integer bookingId, @RequestBody @Valid BookingAcceptedDto dto) {
        log.info("acceptBooking - bookingId:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingStatusService.bookingFromNewToReserved(bookingId, dto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PatchMapping("confirm/{bookingId}")
    public BookingDto confirmBooking(@PathVariable Integer bookingId, @RequestBody @Valid BookingConfirmedDto dto) {
        log.info("confirmBooking - id:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingStatusService.bookingFromReservedToOccupied(bookingId, dto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PatchMapping("reject/{bookingId}")
    public BookingDto rejectBooking(@PathVariable Integer bookingId, @RequestBody @Valid BookingStatusUpdateDto dto) {
        log.info("rejectBooking - id:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingStatusService.bookingRejected(bookingId, dto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PatchMapping("send-warning/{bookingId}")
    public BookingDto sendBookingWarning(@PathVariable Integer bookingId, @RequestBody @Valid BookingWarningDto dto) {
        log.info("sendBookingWarning - id:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingStatusService.sendBookingWarning(bookingId, dto));
    }

    //USER

    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBooking(#bookingId)")
    @PatchMapping("cancel/{bookingId}")
    public BookingDto cancelBooking(@PathVariable Integer bookingId, @RequestBody @Valid BookingStatusUpdateDto dto) {
        log.info("cancelBooking - id:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingStatusService.cancelBooking(bookingId, dto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBooking(#bookingId)")
    @PatchMapping("documents-accepted/{bookingId}")
    public BookingDto sendDocumentConfirmation(@PathVariable Integer bookingId) {
        log.info("sendDocumentConfirmation - id:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingStatusService.confirmDocuments(bookingId));
    }
}
