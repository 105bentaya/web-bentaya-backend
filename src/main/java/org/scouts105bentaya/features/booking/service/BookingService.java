package org.scouts105bentaya.features.booking.service;

import jakarta.persistence.EntityNotFoundException;
import org.joda.time.Interval;
import org.scouts105bentaya.features.booking.converter.BookingFormConverter;
import org.scouts105bentaya.features.booking.dto.BookingDateDto;
import org.scouts105bentaya.features.booking.dto.BookingDateFormDto;
import org.scouts105bentaya.features.booking.dto.BookingFormDto;
import org.scouts105bentaya.features.booking.dto.BookingStatusUpdateDto;
import org.scouts105bentaya.features.booking.dto.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.dto.SimpleBookingDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.ScoutCenter;
import org.scouts105bentaya.core.exception.BasicMessageException;
import org.scouts105bentaya.core.exception.PdfCreationException;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.features.booking.util.BookingIntervalHelper;
import org.scouts105bentaya.features.booking.util.IntervalUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingFormConverter bookingFormConverter;
    private final BookingRepository bookingRepository;
    private final BookingDocumentRepository bookingDocumentRepository;
    private final BookingStatusService bookingStatusService;
    private final AuthService authService;

    public BookingService(
        BookingFormConverter bookingFormConverter,
        BookingRepository bookingRepository,
        BookingStatusService bookingStatusService,
        BookingDocumentRepository bookingDocumentRepository,
        AuthService authService
    ) {
        this.bookingFormConverter = bookingFormConverter;
        this.bookingRepository = bookingRepository;
        this.bookingStatusService = bookingStatusService;
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.authService = authService;
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public List<Booking> findAllByCurrentUser() {
        return bookingRepository.findBookingByUserId(authService.getLoggedUser().getId());
    }

    public Booking findLatestByCurrentUser() {
        return bookingRepository.findFirstByUserIdOrderByCreationDateDesc(authService.getLoggedUser().getId())
            .orElseThrow(() -> new BasicMessageException("No se han encontrado reservas asociadas a este usuario"));
    }

    public Booking findById(Integer id) {
        return bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<BookingDocument> findDocumentsByBookingId(Integer id) {
        return bookingDocumentRepository.findByBookingId(id);
    }

    // Diagrama de estados
    // https://drive.google.com/file/d/10-IZ8xB_kYPx5XaoRHSfIKLbIq9vOo5q/view?usp=sharing
    public Booking updateStatusByManager(BookingStatusUpdateDto newStatusDto) {
        Booking currentBooking = this.findById(newStatusDto.getId());
        if (currentBooking.isOwnBooking()) {
            throw new BasicMessageException("No se puede actualizar una reserva propia");
        }
        if (currentBooking.getStatus() == BookingStatus.NEW) {
            if (newStatusDto.getNewStatus() == BookingStatus.REJECTED) {
                return bookingStatusService.bookingFromNewToRejected(currentBooking, newStatusDto.getObservations());
            } else if (newStatusDto.getNewStatus() == BookingStatus.RESERVED) {
                return bookingStatusService.bookingFromNewToReserved(currentBooking, newStatusDto.getObservations(), newStatusDto.getPrice());
            }
        } else if (currentBooking.getStatus() == BookingStatus.RESERVED) {
            if (newStatusDto.getNewStatus() == BookingStatus.REJECTED) {
                return bookingStatusService.bookingFromNewToRejected(currentBooking, newStatusDto.getObservations());
            } else if (newStatusDto.getNewStatus() == BookingStatus.OCCUPIED || newStatusDto.getNewStatus() == BookingStatus.FULLY_OCCUPIED) {
                return bookingStatusService.bookingFromReservedToOccupied(
                    currentBooking,
                    newStatusDto.getObservations(),
                    newStatusDto.getNewStatus() == BookingStatus.FULLY_OCCUPIED
                );
            } else if (newStatusDto.getNewStatus() == BookingStatus.RESERVED) {
                return this.bookingStatusService.bookingFromReservedToReservedByManager(currentBooking, newStatusDto.getObservations());
            }
        } else if (currentBooking.getStatus() == BookingStatus.OCCUPIED || currentBooking.getStatus() == BookingStatus.FULLY_OCCUPIED) {
            if (newStatusDto.getNewStatus() == BookingStatus.FINISHED) {
                return this.bookingStatusService.bookingToFinished(currentBooking, newStatusDto.getObservations());
            }
        } else if (currentBooking.getStatus() == BookingStatus.LEFT && newStatusDto.getNewStatus() == BookingStatus.FINISHED) {
            return this.bookingStatusService.bookingToFinished(currentBooking, newStatusDto.getObservations());
        }

        throw new BasicMessageException("Estado inválido");
    }

    public Booking updateStatusByUser(BookingStatusUpdateDto newStatusDto) {
        Booking currentBooking = this.findById(newStatusDto.getId());
        if (currentBooking.isOwnBooking()) {
            throw new BasicMessageException("No se puede actualizar una reserva propia");
        }
        if (currentBooking.getStatus() == BookingStatus.NEW) {
            if (newStatusDto.getNewStatus() == BookingStatus.CANCELED) {
                return this.bookingStatusService.bookingCanceled(currentBooking, newStatusDto.getObservations());
            }
        } else if (currentBooking.getStatus() == BookingStatus.RESERVED) {
            if (newStatusDto.getNewStatus() == BookingStatus.CANCELED) {
                return this.bookingStatusService.bookingCanceled(currentBooking, newStatusDto.getObservations());
            } else if (newStatusDto.getNewStatus() == BookingStatus.RESERVED && !currentBooking.isUserConfirmedDocuments()) {
                return this.bookingStatusService.bookingFromReservedToReservedByUser(currentBooking);
            }
        } else if (currentBooking.getStatus() == BookingStatus.OCCUPIED || currentBooking.getStatus() == BookingStatus.FULLY_OCCUPIED) {
            if (newStatusDto.getNewStatus() == BookingStatus.CANCELED) {
                return this.bookingStatusService.bookingCanceled(currentBooking, newStatusDto.getObservations());
            } else if (newStatusDto.getNewStatus() == BookingStatus.LEFT) {
                return this.bookingStatusService.bookingFromOccupiedToLeftByUser(currentBooking, newStatusDto.getObservations());
            }
        }
        throw new BasicMessageException("Estado inválido");
    }

    public List<SimpleBookingDto> getReservationDates(ScoutCenter scoutCenter) {
        return this.bookingRepository
            .findBookingByScoutCenterAndEndDateIsAfter(scoutCenter, LocalDateTime.now()/*.plusDays(7)*/)
            .stream().filter(booking -> booking.getStatus().shouldShowInInformationCalendar())
            .map(this::createReservationDate).toList();
    }

    private SimpleBookingDto createReservationDate(Booking booking) {
        return new SimpleBookingDto(
            booking.getStartDate(),
            booking.getEndDate(),
            booking.getStatus()
        );
    }

    public List<BookingDateDto> getBookingDates(ScoutCenter scoutCenter) {
        return this.bookingRepository
            .findBookingByScoutCenter(scoutCenter).stream()
            .map(booking -> new BookingDateDto(
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus(),
                booking.getId(),
                booking.getPacks()
            )).toList();
    }

    public void addOwnBooking(OwnBookingFormDto dto) {
        this.saveOwnBooking(dto, new Booking());
    }

    public void updateOwnBooking(OwnBookingFormDto dto, Integer id) {
        Booking booking = this.bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!booking.isOwnBooking()) throw new BasicMessageException("No se puede editar una reserva ajena");
        this.saveOwnBooking(dto, booking);
    }

    private void saveOwnBooking(OwnBookingFormDto dto, Booking booking) {
        booking.setStartDate(dto.startDate());
        booking.setEndDate(dto.endDate());
        booking.setScoutCenter(dto.scoutCenter());
        booking.setPacks(dto.packs());
        booking.setObservations(dto.observations());
        booking.setExclusiveReservation(dto.exclusiveReservation());
        booking.setOwnBooking(true);

        if (!dto.endDate().isAfter(dto.startDate())) {
            throw new BasicMessageException("La fecha de salida no puede ser posterior a la de entrada.");
        }
        if (this.dateIsAlreadyTaken(booking)) {
            throw new BasicMessageException("Algunas de las fechas seleccionadas no están disponibles.");
        }

        booking.setStatus(dto.exclusiveReservation() ? BookingStatus.FULLY_OCCUPIED : BookingStatus.OCCUPIED);
        booking.setCreationDate(ZonedDateTime.now());

        bookingRepository.save(booking);
    }

    public Booking cancelOwnBooking(Integer id, String reason) {
        Booking booking = this.bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!booking.isOwnBooking()) throw new BasicMessageException("No se puede cancelar una reserva ajena");
        if (booking.getStatus() == BookingStatus.CANCELED)
            throw new BasicMessageException("No se puede cancelar una reserva cancelada");
        booking.setStatus(BookingStatus.CANCELED);
        booking.setStatusObservations(reason);
        return bookingRepository.save(booking);
    }

    public void saveFromForm(BookingFormDto dto) {
        Booking booking = bookingFormConverter.convertFromDto(dto);

        if (!dto.endDate().isAfter(dto.startDate())) {
            throw new BasicMessageException("La fecha de salida no puede ser posterior a la de entrada.");
        }
        if (dateIsAlreadyTaken(booking)) {
            throw new BasicMessageException("Algunas de las fechas seleccionadas no están disponibles.");
        }

        this.bookingStatusService.saveFromForm(booking);
    }

    private boolean dateIsAlreadyTaken(Booking booking) {
        List<Booking> sameCenterBookings = this.bookingRepository.findBookingByScoutCenterAndEndDateIsAfterAndStartDateIsBefore(
            booking.getScoutCenter(), booking.getStartDate().minusMinutes(1), booking.getEndDate().plusMinutes(1)
        );
        Interval mainInterval = IntervalUtils.intervalFromBooking(booking);
        BookingIntervalHelper helper = new BookingIntervalHelper(sameCenterBookings, mainInterval);
        return helper.overlapsWithFullyOccupiedBooking();
    }

    public List<SimpleBookingDto> getBookingDatesForm(BookingDateFormDto dto) {
        List<Booking> sameCenterBookings = this.bookingRepository.findBookingByScoutCenterAndEndDateIsAfterAndStartDateIsBefore(
            dto.scoutCenter(), dto.startDate().minusMinutes(1), dto.endDate().plusMinutes(1)
        );
        Interval mainInterval = IntervalUtils.intervalFromBooking(dto);
        BookingIntervalHelper helper = new BookingIntervalHelper(sameCenterBookings, mainInterval);
        return helper.getOverlappingBookingIntervals();
    }

    public void saveBookingDocument(Integer bookingId, MultipartFile file) {
        try {
            Booking booking = this.findById(bookingId);
            if (booking.getStatus() != BookingStatus.RESERVED && booking.getStatus() != BookingStatus.OCCUPIED && booking.getStatus() != BookingStatus.FULLY_OCCUPIED)
                throw new BasicMessageException("No se añadir documentos en este paso de la reserva");
            if (booking.getStatus() == BookingStatus.RESERVED && booking.isUserConfirmedDocuments())
                throw new BasicMessageException("No se pueden añadir más documentos si ya los ha confirmado");
            BookingDocument document = new BookingDocument();
            document.setBooking(booking);
            document.setFileName(file.getOriginalFilename());
            document.setStatus(BookingDocumentStatus.PENDING);
            document.setFileData(file.getBytes());
            bookingDocumentRepository.save(document);
        } catch (IOException e) {
            throw new BasicMessageException("File could not be saved");
        }
    }

    private BookingDocument findBookingDocumentById(Integer documentId) {
        return this.bookingDocumentRepository.findById(documentId).orElseThrow(() -> new BasicMessageException("Booking Document not found"));
    }

    public void updateBookingDocument(Integer id, BookingDocumentStatus status) {
        BookingDocument bookingDocument = this.findBookingDocumentById(id);
        bookingDocument.setStatus(status);
        this.bookingDocumentRepository.save(bookingDocument);
    }

    public void deleteDocument(Integer id) {
        this.bookingDocumentRepository.deleteById(id);
    }

    public ResponseEntity<byte[]> getPDF(Integer id) {
        BookingDocument bookingDocument = this.findBookingDocumentById(id);
        try {
            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(bookingDocument.getFileData());
        } catch (Exception ignored) {
            throw new PdfCreationException();
        }
    }
}
