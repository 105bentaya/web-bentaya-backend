package org.scouts105bentaya.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.joda.time.Interval;
import org.scouts105bentaya.converter.booking.BookingFormConverter;
import org.scouts105bentaya.dto.booking.BookingDateDto;
import org.scouts105bentaya.dto.booking.BookingDateFormDto;
import org.scouts105bentaya.dto.booking.BookingFormDto;
import org.scouts105bentaya.dto.booking.BookingStatusUpdateDto;
import org.scouts105bentaya.dto.booking.OwnBookingFormDto;
import org.scouts105bentaya.dto.booking.SimpleBookingDto;
import org.scouts105bentaya.entity.Booking;
import org.scouts105bentaya.entity.BookingDocument;
import org.scouts105bentaya.enums.BookingDocumentStatus;
import org.scouts105bentaya.enums.ScoutCenter;
import org.scouts105bentaya.exception.BasicMessageException;
import org.scouts105bentaya.exception.PdfCreationException;
import org.scouts105bentaya.repository.BookingDocumentRepository;
import org.scouts105bentaya.repository.BookingRepository;
import org.scouts105bentaya.service.AuthService;
import org.scouts105bentaya.service.BookingService;
import org.scouts105bentaya.service.BookingStatusService;
import org.scouts105bentaya.service.EmailService;
import org.scouts105bentaya.service.UserService;
import org.scouts105bentaya.util.BookingIntervalHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.scouts105bentaya.enums.BookingDocumentStatus.PENDING;
import static org.scouts105bentaya.enums.BookingStatus.CANCELED;
import static org.scouts105bentaya.enums.BookingStatus.FINISHED;
import static org.scouts105bentaya.enums.BookingStatus.FULLY_OCCUPIED;
import static org.scouts105bentaya.enums.BookingStatus.LEFT;
import static org.scouts105bentaya.enums.BookingStatus.NEW;
import static org.scouts105bentaya.enums.BookingStatus.OCCUPIED;
import static org.scouts105bentaya.enums.BookingStatus.REJECTED;
import static org.scouts105bentaya.enums.BookingStatus.RESERVED;
import static org.scouts105bentaya.util.IntervalUtils.intervalFromBooking;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingFormConverter bookingFormConverter;
    private final BookingRepository bookingRepository;
    private final BookingDocumentRepository bookingDocumentRepository;
    private final BookingStatusService bookingStatusService;
    private final AuthService authService;

    public BookingServiceImpl(BookingFormConverter bookingFormConverter, BookingRepository bookingRepository, BookingStatusService bookingStatusService, TemplateEngine htmlTemplateEngine, UserService userService, EmailService emailService, BookingDocumentRepository bookingDocumentRepository, AuthService authService) {
        this.bookingFormConverter = bookingFormConverter;
        this.bookingRepository = bookingRepository;
        this.bookingStatusService = bookingStatusService;
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.authService = authService;
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> findAllByCurrentUser() {
        return bookingRepository.findBookingByUserId(authService.getLoggedUser().getId());
    }

    @Override
    public Booking findLatestByCurrentUser() {
        return bookingRepository.findFirstByUserIdOrderByCreationDateDesc(authService.getLoggedUser().getId())
                .orElseThrow(() -> new BasicMessageException("No se han encontrado reservas asociadas a este usuario"));
    }

    @Override
    public Booking findById(Integer id) {
        return bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<BookingDocument> findDocumentsByBookingId(Integer id) {
        return bookingDocumentRepository.findByBookingId(id);
    }

    // Diagrama de estados
    // https://drive.google.com/file/d/10-IZ8xB_kYPx5XaoRHSfIKLbIq9vOo5q/view?usp=sharing
    @Override
    public Booking updateStatusByManager(BookingStatusUpdateDto newStatusDto) {
        Booking currentBooking = this.findById(newStatusDto.getId());
        if (currentBooking.isOwnBooking()) {
            throw new BasicMessageException("No se puede actualizar una reserva propia");
        }
        if (currentBooking.getStatus() == NEW) {
            if (newStatusDto.getNewStatus() == REJECTED) {
                return bookingStatusService.bookingFromNewToRejected(currentBooking, newStatusDto.getObservations());
            } else if (newStatusDto.getNewStatus() == RESERVED) {
                return bookingStatusService.bookingFromNewToReserved(currentBooking, newStatusDto.getObservations(), newStatusDto.getPrice());
            }
        } else if (currentBooking.getStatus() == RESERVED) {
            if (newStatusDto.getNewStatus() == REJECTED) {
                return bookingStatusService.bookingFromNewToRejected(currentBooking, newStatusDto.getObservations());
            } else if (newStatusDto.getNewStatus() == OCCUPIED || newStatusDto.getNewStatus() == FULLY_OCCUPIED) {
                return bookingStatusService.bookingFromReservedToOccupied(
                        currentBooking,
                        newStatusDto.getObservations(),
                        newStatusDto.getNewStatus() == FULLY_OCCUPIED
                );
            } else if (newStatusDto.getNewStatus() == RESERVED) {
                return this.bookingStatusService.bookingFromReservedToReservedByManager(currentBooking, newStatusDto.getObservations());
            }
        } else if (currentBooking.getStatus() == OCCUPIED || currentBooking.getStatus() == FULLY_OCCUPIED) {
            if (newStatusDto.getNewStatus() == FINISHED) {
                return this.bookingStatusService.bookingToFinished(currentBooking, newStatusDto.getObservations());
            }
        } else if (currentBooking.getStatus() == LEFT && newStatusDto.getNewStatus() == FINISHED) {
                return this.bookingStatusService.bookingToFinished(currentBooking, newStatusDto.getObservations());
            }

        throw new BasicMessageException("Estado inválido");
    }

    @Override
    public Booking updateStatusByUser(BookingStatusUpdateDto newStatusDto) {
        Booking currentBooking = this.findById(newStatusDto.getId());
        if (currentBooking.isOwnBooking()) {
            throw new BasicMessageException("No se puede actualizar una reserva propia");
        }
        if (currentBooking.getStatus() == NEW) {
            if (newStatusDto.getNewStatus() == CANCELED) {
                return this.bookingStatusService.bookingCanceled(currentBooking, newStatusDto.getObservations());
            }
        } else if (currentBooking.getStatus() == RESERVED) {
            if (newStatusDto.getNewStatus() == CANCELED) {
                return this.bookingStatusService.bookingCanceled(currentBooking, newStatusDto.getObservations());
            } else if (newStatusDto.getNewStatus() == RESERVED && !currentBooking.isUserConfirmedDocuments()) {
                return this.bookingStatusService.bookingFromReservedToReservedByUser(currentBooking);
            }
        } else if (currentBooking.getStatus() == OCCUPIED || currentBooking.getStatus() == FULLY_OCCUPIED) {
            if (newStatusDto.getNewStatus() == CANCELED) {
                return this.bookingStatusService.bookingCanceled(currentBooking, newStatusDto.getObservations());
            } else if (newStatusDto.getNewStatus() == LEFT) {
                return this.bookingStatusService.bookingFromOccupiedToLeftByUser(currentBooking, newStatusDto.getObservations());
            }
        }
        throw new BasicMessageException("Estado inválido");
    }

    @Override
    public List<SimpleBookingDto> getReservationDates(ScoutCenter scoutCenter) {
        return this.bookingRepository
                .findBookingByScoutCenterAndEndDateIsAfter(scoutCenter, LocalDateTime.now()/*.plusDays(7)*/)
                .stream().filter(booking -> booking.getStatus().shouldShowInInformationCalendar())
                .map(this::createReservationDate).toList();
    }

    private SimpleBookingDto createReservationDate(Booking booking) {
        SimpleBookingDto dto = new SimpleBookingDto();
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    @Override
    public List<BookingDateDto> getBookingDates(ScoutCenter scoutCenter) {
        return this.bookingRepository
                .findBookingByScoutCenter(scoutCenter)
                .stream().map(booking -> {
                    BookingDateDto dto = new BookingDateDto();
                    dto.setStartDate(booking.getStartDate());
                    dto.setEndDate(booking.getEndDate());
                    dto.setStatus(booking.getStatus());
                    dto.setPacks(booking.getPacks());
                    dto.setId(booking.getId());
                    return dto;
                }).toList();
    }

    @Override
    public void addOwnBooking(OwnBookingFormDto dto) {
        this.saveOwnBooking(dto, new Booking());
    }

    @Override
    public void updateOwnBooking(OwnBookingFormDto dto, Integer id) {
        Booking booking = this.bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!booking.isOwnBooking()) throw new BasicMessageException("No se puede editar una reserva ajena");
        this.saveOwnBooking(dto, booking);
    }

    private void saveOwnBooking(OwnBookingFormDto dto, Booking booking) {
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setScoutCenter(dto.getScoutCenter());
        booking.setPacks(dto.getPacks());
        booking.setObservations(dto.getObservations());
        booking.setExclusiveReservation(dto.isExclusiveReservation());
        booking.setOwnBooking(true);

        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new BasicMessageException("La fecha de salida no puede ser posterior a la de entrada.");
        }
        if (this.dateIsAlreadyTaken(booking)) {
            throw new BasicMessageException("Algunas de las fechas seleccionadas no están disponibles.");
        }

        booking.setStatus(dto.isExclusiveReservation() ? FULLY_OCCUPIED : OCCUPIED);
        booking.setCreationDate(ZonedDateTime.now());

        bookingRepository.save(booking);
    }

    @Override
    public Booking cancelOwnBooking(Integer id, String reason) {
        Booking booking = this.bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!booking.isOwnBooking()) throw new BasicMessageException("No se puede cancelar una reserva ajena");
        if (booking.getStatus() == CANCELED) throw new BasicMessageException("No se puede cancelar una reserva cancelada");
        booking.setStatus(CANCELED);
        booking.setStatusObservations(reason);
        return bookingRepository.save(booking);
    }

    @Override
    public void saveFromForm(BookingFormDto dto) {
        Booking booking = bookingFormConverter.convertFromDto(dto);

        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
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
        Interval mainInterval = intervalFromBooking(booking);
        BookingIntervalHelper helper = new BookingIntervalHelper(sameCenterBookings, mainInterval);
        return helper.overlapsWithFullyOccupiedBooking();
    }

    @Override
    public List<SimpleBookingDto> getBookingDatesForm(BookingDateFormDto dto) {
        List<Booking> sameCenterBookings = this.bookingRepository.findBookingByScoutCenterAndEndDateIsAfterAndStartDateIsBefore(
                dto.getScoutCenter(), dto.getStartDate().minusMinutes(1), dto.getEndDate().plusMinutes(1)
        );
        Interval mainInterval = intervalFromBooking(dto);
        BookingIntervalHelper helper = new BookingIntervalHelper(sameCenterBookings, mainInterval);
        return helper.getOverlappingBookingIntervals();
    }

    @Override
    public void saveBookingDocument(Integer bookingId, MultipartFile file) {
        try {
            Booking booking = this.findById(bookingId);
            if (booking.getStatus() != RESERVED && booking.getStatus() != OCCUPIED && booking.getStatus() != FULLY_OCCUPIED) throw new BasicMessageException("No se añadir documentos en este paso de la reserva");
            if (booking.getStatus() == RESERVED && booking.isUserConfirmedDocuments()) throw new BasicMessageException("No se pueden añadir más documentos si ya los ha confirmado");
            BookingDocument document = new BookingDocument();
            document.setBooking(booking);
            document.setFileName(file.getOriginalFilename());
            document.setStatus(PENDING);
            document.setFileData(file.getBytes());
            bookingDocumentRepository.save(document);
        } catch (IOException e) {
            throw new BasicMessageException("File could not be saved");
        }
    }

    private BookingDocument findBookingDocumentById(Integer documentId) {
        return this.bookingDocumentRepository.findById(documentId).orElseThrow(() -> new BasicMessageException("Booking Document not found"));
    }

    @Override
    public void updateBookingDocument(Integer id, BookingDocumentStatus status) {
        BookingDocument bookingDocument = this.findBookingDocumentById(id);
        bookingDocument.setStatus(status);
        this.bookingDocumentRepository.save(bookingDocument);
    }

    @Override
    public void deleteDocument(Integer id) {
        this.bookingDocumentRepository.deleteById(id);
    }

    @Override
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
