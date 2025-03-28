package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.converter.BookingConverter;
import org.scouts105bentaya.features.booking.converter.BookingFormConverter;
import org.scouts105bentaya.features.booking.dto.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.PendingBookingsDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDateFormDto;
import org.scouts105bentaya.features.booking.dto.in.BookingFormDto;
import org.scouts105bentaya.features.booking.dto.in.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.booking.specification.BookingSpecification;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.features.booking.util.BookingIntervalHelper;
import org.scouts105bentaya.features.booking.util.IntervalUtils;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
public class BookingService {

    private final BookingFormConverter bookingFormConverter;
    private final BookingRepository bookingRepository;
    private final BookingStatusService bookingStatusService;
    private final BookingDocumentRepository bookingDocumentRepository;
    private final AuthService authService;
    private final BlobService blobService;
    private final BookingConverter bookingConverter;
    private final ScoutCenterRepository scoutCenterRepository;
    private final SettingService settingService;

    public BookingService(
        BookingFormConverter bookingFormConverter,
        BookingRepository bookingRepository,
        BookingStatusService bookingStatusService,
        BookingDocumentRepository bookingDocumentRepository,
        AuthService authService,
        BlobService blobService,
        BookingConverter bookingConverter,
        ScoutCenterRepository scoutCenterRepository,
        SettingService settingService
    ) {
        this.bookingFormConverter = bookingFormConverter;
        this.bookingRepository = bookingRepository;
        this.bookingStatusService = bookingStatusService;
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.authService = authService;
        this.blobService = blobService;
        this.bookingConverter = bookingConverter;
        this.scoutCenterRepository = scoutCenterRepository;
        this.settingService = settingService;
    }

    public Page<Booking> findAll(BookingSpecificationFilter filter) {
        return bookingRepository.findAll(new BookingSpecification(filter), filter.getPageable());
    }

    public List<Booking> findAllByCurrentUser() {
        return bookingRepository.findBookingByUserId(authService.getLoggedUser().getId());
    }

    public Booking findLatestByCurrentUser() {
        return bookingRepository.findFirstByUserIdOrderByCreationDateDesc(authService.getLoggedUser().getId()).orElseThrow(() -> new WebBentayaNotFoundException("No se han encontrado reservas asociadas a este usuario"));
    }

    public List<BookingDocument> findDocumentsByBookingId(Integer id) {
        return bookingDocumentRepository.findByBookingId(id);
    }

    public List<BookingDateAndStatusDto> getReservationDates(Integer scoutCenterId) {
        return this.bookingRepository.findBookingByScoutCenterIdAndEndDateIsAfter(scoutCenterId, LocalDateTime.now()/*.plusDays(7)*/).stream().filter(booking -> booking.getStatus().reservedOrOccupied()).map(this::createReservationDate).toList();
    }

    private BookingDateAndStatusDto createReservationDate(Booking booking) {
        return new BookingDateAndStatusDto(booking.getStartDate(), booking.getEndDate(), booking.getStatus(), booking.isExclusiveReservation());
    }

    public List<BookingCalendarInfoDto> getBookingDates(BookingSpecificationFilter filter) {
        return this.bookingRepository.findAll(new BookingSpecification(filter)).stream()
            .map(BookingCalendarInfoDto::fromBooking).toList();
    }

    public void addOwnBooking(OwnBookingFormDto dto) {
        this.saveOwnBooking(dto, new Booking());
    }

    public void updateOwnBooking(OwnBookingFormDto dto, Integer id) {//todo revisar
        Booking booking = this.bookingRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        if (!booking.isOwnBooking()) {
            log.warn("updateOwnBooking - booking is not own booking");
            throw new WebBentayaBadRequestException("No se puede editar una reserva ajena");
        }
        this.saveOwnBooking(dto, booking);
    }

    private void saveOwnBooking(OwnBookingFormDto dto, Booking booking) {
        booking.setStartDate(dto.startDate());
        booking.setEndDate(dto.endDate());
        booking.setScoutCenter(scoutCenterRepository.get(dto.scoutCenterId()));
        booking.setPacks(dto.packs());
        booking.setObservations(dto.observations());
        booking.setExclusiveReservation(dto.exclusiveReservation());
        booking.setOwnBooking(true);

        this.validateBookingDates(booking);

        booking.setStatus(BookingStatus.OCCUPIED);
        booking.setCreationDate(ZonedDateTime.now());

        bookingRepository.save(booking);
    }

    public Booking cancelOwnBooking(Integer id, String reason) {
        Booking booking = this.bookingRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        if (!booking.isOwnBooking()) {
            log.warn("cancelOwnBooking - booking is not own booking");
            throw new WebBentayaBadRequestException("No se puede cancelar una reserva ajena");
        }
        if (booking.getStatus() == BookingStatus.CANCELED) {
            log.warn("cancelOwnBooking - booking is already canceled");
            throw new WebBentayaBadRequestException("No se puede cancelar una reserva cancelada");
        }
        booking.setStatus(BookingStatus.CANCELED);
        booking.setStatusObservations(reason);
        return bookingRepository.save(booking);
    }

    public void saveFromForm(BookingFormDto dto) {
        Booking booking = bookingFormConverter.convertFromDto(dto);
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

    private void validateBookingDates(Booking booking) {
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

    public void saveBookingDocument(Integer bookingId, MultipartFile file) {
        Booking booking = this.bookingRepository.get(bookingId);
        if (!booking.getStatus().reservedOrOccupied()) {
            log.warn("saveBookingDocument - booking status {} is not valid for uploading documents", booking.getStatus());
            throw new WebBentayaBadRequestException("No se añadir documentos en este paso de la reserva");
        }
        BookingDocument document = new BookingDocument();
        document.setBooking(booking);
        document.setFileName(file.getOriginalFilename());
        document.setStatus(BookingDocumentStatus.PENDING);
        document.setFileUuid(blobService.createBlob(file));
        bookingDocumentRepository.save(document);
    }

    private BookingDocument findBookingDocumentById(Integer documentId) {
        return this.bookingDocumentRepository.findById(documentId).orElseThrow(() -> new WebBentayaBadRequestException("Booking Document not found"));
    }

    public void updateBookingDocument(Integer id, BookingDocumentStatus status) {
        BookingDocument bookingDocument = this.findBookingDocumentById(id);
        bookingDocument.setStatus(status);
        this.bookingDocumentRepository.save(bookingDocument);
    }

    public void deleteDocument(Integer id) {
        BookingDocument bookingDocument = this.findBookingDocumentById(id);
        this.blobService.deleteBlob(bookingDocument.getFileUuid());
        this.bookingDocumentRepository.delete(bookingDocument);
    }

    public ResponseEntity<byte[]> getBookingDocument(Integer id) {
        BookingDocument bookingDocument = this.findBookingDocumentById(id);
        return new FileTransferDto(
            blobService.getBlob(bookingDocument.getFileUuid()),
            bookingDocument.getFileName(),
            MediaType.APPLICATION_PDF
        ).asResponseEntity();
    }

    public PendingBookingsDto findAllPending(BookingSpecificationFilter filter) {
        filter.setSortedBy("creationDate");

        filter.setStatuses(List.of(BookingStatus.NEW));
        List<BookingDto> newBookings = getBookingDtoList(filter);

        filter.setSortedBy("startDate");
        filter.setStatuses(List.of(BookingStatus.RESERVED));
        List<BookingDto> acceptedBookings = getBookingDtoList(filter);

        filter.setStatuses(List.of(BookingStatus.OCCUPIED));
        filter.setEndDate(LocalDateTime.now().toString());
        List<BookingDto> confirmedBookings = getBookingDtoList(filter);

        filter.setStatuses(List.of(BookingStatus.OCCUPIED));
        filter.setEndDate(null);
        filter.setFilterDates(new String[]{LocalDateTime.now().minusDays(3).toString(), LocalDateTime.now().toString()});
        List<BookingDto> finishedBookings = getBookingDtoList(filter);

        return new PendingBookingsDto(newBookings, acceptedBookings, confirmedBookings, finishedBookings);
    }

    private List<BookingDto> getBookingDtoList(BookingSpecificationFilter filter) {
        return bookingConverter.convertEntityCollectionToDtoList(bookingRepository.findAll(new BookingSpecification(filter)));
    }
}
