package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.converter.BookingFormConverter;
import org.scouts105bentaya.features.booking.dto.data.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.data.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.dto.data.BookingInfoDto;
import org.scouts105bentaya.features.booking.dto.data.PendingBookingsDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDateFormDto;
import org.scouts105bentaya.features.booking.dto.in.BookingFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.booking.repository.GeneralBookingRepository;
import org.scouts105bentaya.features.booking.specification.BookingSpecification;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.features.booking.util.BookingIntervalHelper;
import org.scouts105bentaya.features.booking.util.IntervalUtils;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
public class BookingService {

    private final BookingFormConverter bookingFormConverter;
    private final BookingRepository bookingRepository;
    private final GeneralBookingRepository generalBookingRepository;
    private final BookingStatusService bookingStatusService;
    private final AuthService authService;
    private final SettingService settingService;

    public BookingService(
        BookingFormConverter bookingFormConverter,
        BookingRepository bookingRepository,
        GeneralBookingRepository generalBookingRepository,
        BookingStatusService bookingStatusService,
        AuthService authService,
        SettingService settingService
    ) {
        this.bookingFormConverter = bookingFormConverter;
        this.bookingRepository = bookingRepository;
        this.generalBookingRepository = generalBookingRepository;
        this.bookingStatusService = bookingStatusService;
        this.authService = authService;
        this.settingService = settingService;
    }

    public Page<Booking> findAll(BookingSpecificationFilter filter) {
        return bookingRepository.findAll(new BookingSpecification(filter), filter.getPageable());
    }

    public GeneralBooking findLatestByCurrentUser() {
        return generalBookingRepository.findFirstByUserIdOrderByCreationDateDesc(authService.getLoggedUser().getId()).orElseThrow(() -> new WebBentayaNotFoundException("No se han encontrado reservas asociadas a este usuario"));
    }

    public List<BookingDateAndStatusDto> getReservationDates(Integer scoutCenterId) {
        return this.bookingRepository.findBookingByScoutCenterIdAndEndDateIsAfter(scoutCenterId, LocalDateTime.now()).stream().filter(booking -> booking.getStatus().reservedOrOccupied()).map(this::createReservationDate).toList();
    }

    private BookingDateAndStatusDto createReservationDate(Booking booking) {
        return new BookingDateAndStatusDto(booking.getStartDate(), booking.getEndDate(), booking.getStatus(), booking.isExclusiveReservation());
    }

    public List<BookingCalendarInfoDto> getBookingDates(BookingSpecificationFilter filter) {
        return this.bookingRepository.findAll(new BookingSpecification(filter)).stream()
            .map(BookingCalendarInfoDto::fromBooking).toList();
    }

    public void saveFromForm(BookingFormDto dto) {
        GeneralBooking booking = bookingFormConverter.convertFromDto(dto);
        this.validateBookingDates(booking);
        this.validateIfDateIsAllowed(booking);
        this.bookingStatusService.saveFromForm(booking);
    }

    private void validateIfDateIsAllowed(Booking booking) {
        String settingDate = settingService.findByName(SettingEnum.BOOKING_DATE).getValue();
        LocalDateTime maxDate = ZonedDateTime.parse(settingDate).toLocalDate().plusDays(1).atStartOfDay();
        if (booking.getStartDate().isAfter(maxDate)) {
            throw new WebBentayaBadRequestException("No se puede hacer una reserva para estas fechas");
        }
    }

    public void validateBookingDates(Booking booking) {
        if (!booking.getEndDate().isAfter(booking.getStartDate())) {
            log.warn("validateBookingDates - booking end date is not after start date");
            throw new WebBentayaBadRequestException("La fecha de salida no puede ser posterior a la de entrada.");
        }
        List<Booking> sameCenterBookings = this.bookingRepository.findAllOverlapping(booking.getStartDate(), booking.getEndDate(), booking.getScoutCenter().getId());
        Interval mainInterval = IntervalUtils.intervalFromBooking(booking);
        if (BookingIntervalHelper.overlapsWithFullyOccupiedBooking(sameCenterBookings, mainInterval)) {
            log.warn("validateBookingDates - selected date is taken");
            throw new WebBentayaBadRequestException("Algunas de las fechas seleccionadas no están disponibles");
        }
    }

    public List<BookingDateAndStatusDto> getScoutCenterBookingDatesStatuses(BookingDateFormDto dto) {
        List<Booking> sameCenterBookings = this.bookingRepository.findAllOverlapping(dto.startDate(), dto.endDate(), dto.scoutCenterId());
        Interval mainInterval = IntervalUtils.intervalFromBooking(dto);
        return BookingIntervalHelper.getOverlappingBookingIntervals(sameCenterBookings, mainInterval);
    }

    public PendingBookingsDto findAllPending(BookingSpecificationFilter filter) {
        filter.setSortedBy("creationDate");

        filter.setStatuses(List.of(BookingStatus.NEW));
        List<BookingInfoDto> newBookings = getBookingDtoList(filter);

        filter.setSortedBy("startDate");
        filter.setStatuses(List.of(BookingStatus.RESERVED));
        List<BookingInfoDto> acceptedBookings = getBookingDtoList(filter);

        filter.setStatuses(List.of(BookingStatus.OCCUPIED));
        filter.setEndDate(LocalDateTime.now().toString());
        List<BookingInfoDto> confirmedBookings = getBookingDtoList(filter);

        filter.setStatuses(List.of(BookingStatus.OCCUPIED));
        filter.setEndDate(null);
        filter.setFilterDates(new String[]{LocalDateTime.now().minusDays(7).toString(), LocalDateTime.now().toString()});
        List<BookingInfoDto> finishedBookings = getBookingDtoList(filter);

        return new PendingBookingsDto(newBookings, acceptedBookings, confirmedBookings, finishedBookings);
    }

    private List<BookingInfoDto> getBookingDtoList(BookingSpecificationFilter filter) {
        return bookingRepository.findAll(new BookingSpecification(filter)).stream().map(BookingInfoDto::fromEntity).toList();
    }
}
