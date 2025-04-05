package org.scouts105bentaya.features.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.dto.BookingDocumentDto;
import org.scouts105bentaya.features.booking.dto.in.BookingDocumentStatusFormDto;
import org.scouts105bentaya.features.booking.entity.BookingDocumentType;
import org.scouts105bentaya.features.booking.repository.BookingDocumentTypeRepository;
import org.scouts105bentaya.features.booking.service.BookingDocumentService;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/booking/document")
public class BookingDocumentController {

    private final BookingDocumentService bookingDocumentService;
    private final BookingDocumentTypeRepository bookingDocumentTypeRepository;

    public BookingDocumentController(
        BookingDocumentService bookingDocumentService,
        BookingDocumentTypeRepository bookingDocumentTypeRepository
    ) {
        this.bookingDocumentService = bookingDocumentService;
        this.bookingDocumentTypeRepository = bookingDocumentTypeRepository;
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER')")
    @GetMapping("/active-types")
    public List<BookingDocumentType> getActiveBookingDocumentTypes() {
        log.info("getActiveBookingDocumentTypes{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingDocumentTypeRepository.findAllByActiveIsTrue();
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/types")
    public List<BookingDocumentType> getAllBookingDocumentTypes() {
        log.info("getAllBookingDocumentTypes{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingDocumentTypeRepository.findAllByOrderByActiveDesc();
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBooking(#bookingId)")
    @GetMapping("/{bookingId}")
    public List<BookingDocumentDto> getBookingDocuments(@PathVariable Integer bookingId) {
        log.info("getBookingDocuments - bookingId: {}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingDocumentService.findDocumentsByBookingId(bookingId);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBookingDocumentFile(#documentId)")
    @GetMapping("/pdf/{documentId}")
    public ResponseEntity<byte[]> getBookingDocument(@PathVariable Integer documentId) {
        log.info("getBookingDocument - documentId: {}{}", documentId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingDocumentService.getBookingDocument(documentId);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBooking(#bookingId)")
    @PostMapping(value = "/{bookingId}", consumes = "multipart/form-data")
    public void uploadBookingDocument(
        @PathVariable Integer bookingId,
        @RequestParam("file") MultipartFile file,
        @RequestParam("typeId") Integer typeId
    ) {
        log.info("uploadBookingDocument - bookingId: {}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        bookingDocumentService.saveBookingDocument(bookingId, file, typeId);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping(value = "/incidences/{bookingId}")
    public ResponseEntity<byte[]> getIncidencesDocument(@PathVariable Integer bookingId) {
        log.info("getIncidencesDocument - bookingId: {}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingDocumentService.getBookingIncidenceFile(bookingId);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBooking(#bookingId)")
    @PostMapping(value = "/incidences/{bookingId}", consumes = "multipart/form-data")
    public void uploadIncidencesDocument(
        @PathVariable Integer bookingId,
        @RequestParam("file") MultipartFile file
    ) {
        log.info("uploadIncidencesDocument - bookingId: {}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        bookingDocumentService.saveBookingIncidencesFile(bookingId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PutMapping("/{id}")
    public BookingDocumentDto updateBookingDocumentStatus(@PathVariable Integer id, @Valid @RequestBody BookingDocumentStatusFormDto form) {
        log.info("updateBookingDocumentStatus{}", SecurityUtils.getLoggedUserUsernameForLog());
        return BookingDocumentDto.fromBookingDocument(bookingDocumentService.updateBookingDocumentStatus(id, form));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userCanEditBookingDocumentFile(#documentId)")
    @DeleteMapping("/{documentId}")
    public void deleteDocument(@PathVariable Integer documentId) {
        log.info("deleteDocument - documentId: {}{}", documentId, SecurityUtils.getLoggedUserUsernameForLog());
        bookingDocumentService.deleteDocument(documentId);
    }
}
