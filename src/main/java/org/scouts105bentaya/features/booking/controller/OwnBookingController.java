package org.scouts105bentaya.features.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.dto.OwnBookingDto;
import org.scouts105bentaya.features.booking.dto.in.BookingConfirmedDto;
import org.scouts105bentaya.features.booking.dto.in.BookingStatusUpdateDto;
import org.scouts105bentaya.features.booking.dto.in.BookingUpdateDto;
import org.scouts105bentaya.features.booking.dto.in.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.repository.OwnBookingRepository;
import org.scouts105bentaya.features.booking.service.OwnBookingService;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/booking/own")
public class OwnBookingController {

    private final OwnBookingService ownBookingService;
    private final OwnBookingRepository ownBookingRepository;

    public OwnBookingController(
        OwnBookingService ownBookingService,
        OwnBookingRepository ownBookingRepository
    ) {
        this.ownBookingService = ownBookingService;
        this.ownBookingRepository = ownBookingRepository;
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping()
    public PageDto<OwnBookingDto> getAll(BookingSpecificationFilter filterDto) {
        log.info("getAll - {}{}", filterDto, SecurityUtils.getLoggedUserUsernameForLog());
        Page<OwnBooking> page = ownBookingService.findAll(filterDto);
        return new PageDto<>(page.getTotalElements(), page.stream().map(OwnBookingDto::fromEntity).toList());
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping("/{bookingId}")
    public OwnBookingDto getById(@PathVariable Integer bookingId) {
        log.info("getById - bookingId:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return OwnBookingDto.fromEntity(ownBookingRepository.get(bookingId));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or (hasAnyRole('SCOUTER', 'GROUP_SCOUTER') and (#formDto.groupId == 0 or @authLogic.scouterHasGroupId(#formDto.groupId)))")
    @PostMapping("/new")
    public void addOwnBooking(@RequestBody @Valid OwnBookingFormDto formDto) {
        log.info("addOwnBooking{}", SecurityUtils.getLoggedUserUsernameForLog());
        ownBookingService.addOwnBooking(formDto);
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER') and @authLogic.scouterHasAccessToBooking(#bookingId)")
    @PatchMapping("/cancel/{bookingId}")
    public OwnBookingDto cancelOwnBooking(@PathVariable Integer bookingId, @RequestBody @Valid BookingStatusUpdateDto dto) {
        log.info("cancelOwnBooking{}", SecurityUtils.getLoggedUserUsernameForLog());
        return OwnBookingDto.fromEntity(ownBookingService.cancelOwnBooking(bookingId, dto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PatchMapping("/confirm/{bookingId}")
    public OwnBookingDto confirmOwnBooking(@PathVariable Integer bookingId, @RequestBody @Valid BookingConfirmedDto dto) {
        log.info("confirmOwnBooking - bookingId:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return OwnBookingDto.fromEntity(ownBookingService.confirmOwnBooking(bookingId, dto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PatchMapping("/reject/{bookingId}")
    public OwnBookingDto rejectOwnBooking(@PathVariable Integer bookingId, @RequestBody @Valid BookingStatusUpdateDto dto) {
        log.info("rejectOwnBooking - bookingId:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return OwnBookingDto.fromEntity(ownBookingService.rejectOwnBooking(bookingId, dto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PatchMapping("update/{bookingId}")
    public void updateBooking(@PathVariable Integer bookingId, @RequestBody @Valid BookingUpdateDto dto) {
        log.info("updateBooking - bookingId:{}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        ownBookingService.updateBooking(bookingId, dto);
    }

}
