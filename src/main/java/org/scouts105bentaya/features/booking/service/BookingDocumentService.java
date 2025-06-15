package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.dto.BookingDocumentDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDocumentStatusFormDto;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.entity.BookingDocumentFile;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.booking.enums.BookingDocumentDuration;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.booking.repository.BookingDocumentFileRepository;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.scouts105bentaya.features.booking.repository.BookingDocumentTypeRepository;
import org.scouts105bentaya.features.booking.repository.GeneralBookingRepository;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileTypeEnum;
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
    private final GeneralBookingRepository generalBookingRepository;
    private final BlobService blobService;
    private final BookingDocumentTypeRepository bookingDocumentTypeRepository;
    private final BookingDocumentFileRepository bookingDocumentFileRepository;
    private final AuthService authService;

    public BookingDocumentService(
        BookingDocumentRepository bookingDocumentRepository,
        GeneralBookingRepository generalBookingRepository,
        BlobService blobService,
        BookingDocumentTypeRepository bookingDocumentTypeRepository,
        BookingDocumentFileRepository bookingDocumentFileRepository,
        AuthService authService
    ) {
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.generalBookingRepository = generalBookingRepository;
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
        FileUtils.validateFileType(file, FileTypeEnum.PDF_TYPE);

        GeneralBooking booking = generalBookingRepository.get(bookingId);

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
        this.deleteDocument(this.bookingDocumentRepository.get(id));
    }

    public void deleteDocument(BookingDocument bookingDocument) {
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

    public ResponseEntity<byte[]> getBookingIncidenceFile(Integer id) {
        GeneralBooking booking = this.generalBookingRepository.get(id);
        if (booking.getIncidencesFile() == null) {
            throw new WebBentayaNotFoundException("La reserva no tiene registro de incidencias asociado");
        }
        BookingDocumentFile file = booking.getIncidencesFile();
        return new FileTransferDto(
            blobService.getBlob(file.getUuid()),
            file.getName(),
            file.getMimeType()
        ).asResponseEntity();
    }
}
