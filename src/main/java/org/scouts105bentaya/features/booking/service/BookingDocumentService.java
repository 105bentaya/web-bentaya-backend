package org.scouts105bentaya.features.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.converter.BookingConverter;
import org.scouts105bentaya.features.booking.converter.BookingFormConverter;
import org.scouts105bentaya.features.booking.dto.BookingDocumentDto;
import org.scouts105bentaya.features.booking.dto.BookingDto;
import org.scouts105bentaya.features.booking.dto.data.BookingCalendarInfoDto;
import org.scouts105bentaya.features.booking.dto.data.BookingDateAndStatusDto;
import org.scouts105bentaya.features.booking.dto.data.PendingBookingsDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDateFormDto;
import org.scouts105bentaya.features.booking.dto.in.BookingFormDto;
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
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
public class BookingDocumentService {

    private final BookingDocumentRepository bookingDocumentRepository;

    public BookingDocumentService(BookingDocumentRepository bookingDocumentRepository) {
        this.bookingDocumentRepository = bookingDocumentRepository;
    }

    public void uploadBookingDocument() {

    }

    public List<BookingDocumentDto> findDocumentsByBookingId(Integer id) {
        return bookingDocumentRepository.findByBookingId(id).stream().map(BookingDocumentDto::fromBooking);
    }

    public void saveBookingDocument(Integer bookingId, MultipartFile file) {
//        Booking booking = this.bookingRepository.get(bookingId);
//        if (!booking.getStatus().reservedOrOccupied()) {
//            log.warn("saveBookingDocument - booking status {} is not valid for uploading documents", booking.getStatus());
//            throw new WebBentayaBadRequestException("No se añadir documentos en este paso de la reserva");
//        }
//        BookingDocument document = new BookingDocument();
//        document.setBooking(booking);
//        document.setFileName(file.getOriginalFilename());
//        document.setStatus(BookingDocumentStatus.PENDING);
//        document.setFileUuid(blobService.createBlob(file));
//        bookingDocumentRepository.save(document);
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
//        BookingDocument bookingDocument = this.findBookingDocumentById(id);
//        this.blobService.deleteBlob(bookingDocument.getFileUuid());
//        this.bookingDocumentRepository.delete(bookingDocument);
    }

    public ResponseEntity<byte[]> getBookingDocument(Integer id) {
//        BookingDocument bookingDocument = this.findBookingDocumentById(id);
//        return new FileTransferDto(
//            blobService.getBlob(bookingDocument.getFileUuid()),
//            bookingDocument.getFileName(),
//            MediaType.APPLICATION_PDF
//        ).asResponseEntity();
        return null;
    }
}
