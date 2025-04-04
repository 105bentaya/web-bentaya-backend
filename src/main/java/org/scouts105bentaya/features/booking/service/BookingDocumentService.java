package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.features.booking.dto.BookingDocumentDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.entity.BookingDocumentFile;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.booking.repository.BookingDocumentFileRepository;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.scouts105bentaya.features.booking.repository.BookingDocumentTypeRepository;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
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

    public BookingDocumentService(BookingDocumentRepository bookingDocumentRepository, BookingRepository bookingRepository, BlobService blobService, BookingDocumentTypeRepository bookingDocumentTypeRepository, BookingDocumentFileRepository bookingDocumentFileRepository) {
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.bookingRepository = bookingRepository;
        this.blobService = blobService;
        this.bookingDocumentTypeRepository = bookingDocumentTypeRepository;
        this.bookingDocumentFileRepository = bookingDocumentFileRepository;
    }

    public List<BookingDocumentDto> findDocumentsByBookingId(Integer id) {
        return bookingDocumentRepository.findByBookingId(id).stream().map(BookingDocumentDto::fromBooking).toList();
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
        document.setFile(bookingDocumentFile);
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
        BookingDocumentFile file = bookingDocument.getFile();
        this.bookingDocumentRepository.delete(bookingDocument);
        if (file.getBookingDocuments().isEmpty()) {
            this.blobService.deleteBlob(file.getUuid());
            bookingDocumentFileRepository.delete(file);
        }
    }

    public ResponseEntity<byte[]> getBookingDocument(Integer id) {
        BookingDocumentFile file = this.findBookingDocumentById(id).getFile();
        return new FileTransferDto(
            blobService.getBlob(file.getUuid()),
            file.getName(),
            file.getMimeType()
        ).asResponseEntity();
    }
}
