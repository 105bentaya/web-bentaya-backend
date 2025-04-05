package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.features.booking.dto.BookingDocumentDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDocumentStatusFormDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.entity.BookingDocumentFile;
import org.scouts105bentaya.features.booking.enums.BookingDocumentDuration;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.booking.repository.BookingDocumentFileRepository;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.scouts105bentaya.features.booking.repository.BookingDocumentTypeRepository;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class BookingDocumentService {

    private final BookingDocumentRepository bookingDocumentRepository;
    private final BookingRepository bookingRepository;
    private final BlobService blobService;
    private final BookingDocumentTypeRepository bookingDocumentTypeRepository;
    private final BookingDocumentFileRepository bookingDocumentFileRepository;
    private final AuthService authService;

    public BookingDocumentService(
        BookingDocumentRepository bookingDocumentRepository,
        BookingRepository bookingRepository,
        BlobService blobService,
        BookingDocumentTypeRepository bookingDocumentTypeRepository,
        BookingDocumentFileRepository bookingDocumentFileRepository,
        AuthService authService
    ) {
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.bookingRepository = bookingRepository;
        this.blobService = blobService;
        this.bookingDocumentTypeRepository = bookingDocumentTypeRepository;
        this.bookingDocumentFileRepository = bookingDocumentFileRepository;
        this.authService = authService;
    }

    public List<BookingDocumentDto> findDocumentsByBookingId(Integer id) {
        User loggedUser = authService.getLoggedUser();
        return loggedUser.hasRole(RoleEnum.ROLE_SCOUT_CENTER_MANAGER) ?
            bookingDocumentRepository.findAllByBookingId(id).stream().map(BookingDocumentDto::fromBookingDocument).toList() :
            bookingDocumentRepository.findAllByBookingId(id).stream()
                .map(doc -> BookingDocumentDto.fromBookingDocumentAndUser(doc, loggedUser))
                .toList();
    }

    public void saveBookingDocument(Integer bookingId, MultipartFile file, Integer typeId) {
        FileUtils.validateFileIsPdf(file);

        Booking booking = bookingRepository.get(bookingId);

        if (!booking.getStatus().reservedOrOccupied()) {
            log.warn("saveBookingDocument - booking status {} is not valid for uploading documents", booking.getStatus());
            throw new WebBentayaBadRequestException("No se pueden añadir documentos en este paso de la reserva");
        }

        BookingDocument document = new BookingDocument();
        document.setBooking(booking);
        document.setType(bookingDocumentTypeRepository.get(typeId));
        document.setStatus(BookingDocumentStatus.PENDING);

        BookingDocumentFile bookingDocumentFile = new BookingDocumentFile();
        bookingDocumentFile.setName(file.getOriginalFilename());
        bookingDocumentFile.setMimeType(file.getContentType());
        bookingDocumentFile.setUuid(blobService.createBlob(file));
        bookingDocumentFile.setUser(authService.getLoggedUser());

        document.setFile(bookingDocumentFile);
        bookingDocumentRepository.save(document);
    }

    public BookingDocument updateBookingDocumentStatus(Integer id, BookingDocumentStatusFormDto statusForm) {
        this.validateStatusForm(statusForm);
        BookingDocument bookingDocument = this.bookingDocumentRepository.get(id);

        bookingDocument.setStatus(statusForm.status());
        bookingDocument.setDuration(statusForm.status() == BookingDocumentStatus.ACCEPTED ? statusForm.duration() : null);
        bookingDocument.setExpirationDate(statusForm.duration() == BookingDocumentDuration.EXPIRABLE ? statusForm.expirationDate() : null);

        return this.bookingDocumentRepository.save(bookingDocument);
    }

    private void validateStatusForm(BookingDocumentStatusFormDto statusForm) {
        if (statusForm.status() == BookingDocumentStatus.ACCEPTED) {
            if (statusForm.duration() == null) {
                throw new WebBentayaBadRequestException("Se debe indicar el tipo de validez del documento");
            }
            if (statusForm.duration() == BookingDocumentDuration.EXPIRABLE && statusForm.expirationDate() == null) {
                throw new WebBentayaBadRequestException("Se debe indicar la fecha caducidad del documento");
            }
        }
    }

    public void deleteDocument(Integer id) {
        BookingDocument bookingDocument = this.bookingDocumentRepository.get(id);
        BookingDocumentFile file = bookingDocument.getFile();
        this.bookingDocumentRepository.delete(bookingDocument);
        if (file.getBookingDocuments().isEmpty()) {
            this.blobService.deleteBlob(file.getUuid());
            bookingDocumentFileRepository.delete(file);
        }
    }

    public ResponseEntity<byte[]> getBookingDocument(Integer id) {
        BookingDocumentFile file = this.bookingDocumentRepository.get(id).getFile();
        return new FileTransferDto(
            blobService.getBlob(file.getUuid()),
            file.getName(),
            file.getMimeType()
        ).asResponseEntity();
    }

    public void saveBookingIncidencesFile(Integer bookingId, MultipartFile file) {
        FileUtils.validateFileIsDocOrPdf(file);
        Booking booking = bookingRepository.get(bookingId);

        if (!booking.getStatus().reservedOrOccupied()) {
            log.warn("saveBookingIncidencesFile - booking status {} is not valid for uploading documents", booking.getStatus());
            throw new WebBentayaBadRequestException("No se puede añadir el registro de incidencias y estados en este paso de la reserva");
        }
        if (booking.getIncidencesFile() != null) {
            log.warn("saveBookingIncidencesFile - booking {} has already an incidence file", booking.getId());
            throw new WebBentayaBadRequestException("No se puede volver a añadir el registro de incidencias y estados");
        }

        BookingDocumentFile bookingDocumentFile = new BookingDocumentFile();
        bookingDocumentFile.setName(file.getOriginalFilename());
        bookingDocumentFile.setMimeType(file.getContentType());
        bookingDocumentFile.setUuid(blobService.createBlob(file));

        booking.setIncidencesFile(bookingDocumentFile);
        bookingRepository.save(booking);
    }
}
