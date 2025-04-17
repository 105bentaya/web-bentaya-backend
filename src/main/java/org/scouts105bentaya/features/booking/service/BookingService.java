package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.features.booking.dto.data.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.data.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.dto.data.BookingInfoDto;
import org.scouts105bentaya.features.booking.dto.data.PendingBookingsDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDateFormDto;
import org.scouts105bentaya.features.booking.dto.in.BookingUpdateDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.booking.repository.GeneralBookingRepository;
import org.scouts105bentaya.features.booking.specification.BookingSpecification;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.features.booking.util.BookingIntervalHelper;
import org.scouts105bentaya.features.booking.util.IntervalUtils;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final GeneralBookingRepository generalBookingRepository;
    private final AuthService authService;
    private final SettingService settingService;
    private final EmailService emailService;
    private final ScoutCenterRepository scoutCenterRepository;

    @Value("${bentaya.web.url}") private String url;

    public BookingService(
        BookingRepository bookingRepository,
        GeneralBookingRepository generalBookingRepository,
        AuthService authService,
        SettingService settingService,
        EmailService emailService,
        ScoutCenterRepository scoutCenterRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.generalBookingRepository = generalBookingRepository;
        this.authService = authService;
        this.settingService = settingService;
        this.emailService = emailService;
        this.scoutCenterRepository = scoutCenterRepository;
    }

    public Page<Booking> findAll(BookingSpecificationFilter filter) {
        return bookingRepository.findAll(new BookingSpecification(filter), filter.getPageable());
    }

    public List<GeneralBooking> findLatestByCurrentUser() {
        return generalBookingRepository.findLatestUserBookings(authService.getLoggedUser().getId());
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

    public void validateIfBookingDateIsOpen(Booking booking) {
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
        if (booking.getId() != null) {
            sameCenterBookings.removeIf(sameCenterBooking -> sameCenterBooking.getId().equals(booking.getId()));
        }
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

    public Context getBookingBasicContext(Booking booking, String subject) {
        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());
        context.setVariable("bookingInformationMail", emailService.getSettingEmails(SettingEnum.BOOKING_MAIL)[0]);
        context.setVariable("webUrl", url);
        context.setVariable("subject", subject);
        context.setVariable("color", booking.getScoutCenter().getColor());

        return context;
    }

    public Map<String, Object> getBookingUpdateBasicDifferences(Booking booking, BookingUpdateDto dto) {
        Map<String, Object> differences = new HashMap<>();
        if (!Objects.equals(dto.startDate(), booking.getStartDate())) {
            booking.setStartDate(dto.startDate());
            differences.put("startDate", booking.getStartDate());
        }
        if (!Objects.equals(dto.endDate(), booking.getEndDate())) {
            booking.setEndDate(dto.endDate());
            differences.put("endDate", booking.getEndDate());
        }
        if (!Objects.equals(dto.packs(), booking.getPacks())) {
            booking.setPacks(dto.packs());
            differences.put("packs", booking.getPacks());
        }
        if (!Objects.equals(dto.scoutCenterId(), booking.getScoutCenter().getId())) {
            booking.setScoutCenter(scoutCenterRepository.get(dto.scoutCenterId()));
            differences.put("centerChanged", booking.getScoutCenter().getName());
        }

        boolean exclusiveness = booking.getScoutCenter().isAlwaysExclusive() || dto.exclusiveReservation();
        if (!Objects.equals(exclusiveness, booking.isExclusiveReservation())) {
            booking.setExclusiveReservation(exclusiveness);
            differences.put("exclusiveness", booking.isExclusiveReservation());
        }
        return differences;
    }
}
