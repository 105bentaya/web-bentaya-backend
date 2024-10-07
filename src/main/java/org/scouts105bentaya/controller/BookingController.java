package org.scouts105bentaya.controller;

import jakarta.validation.Valid;
import org.scouts105bentaya.converter.booking.BookingConverter;
import org.scouts105bentaya.converter.booking.BookingDocumentConverter;
import org.scouts105bentaya.dto.booking.BookingDateDto;
import org.scouts105bentaya.dto.booking.BookingDateFormDto;
import org.scouts105bentaya.dto.booking.BookingDocumentDto;
import org.scouts105bentaya.dto.booking.BookingDto;
import org.scouts105bentaya.dto.booking.BookingFormDto;
import org.scouts105bentaya.dto.booking.BookingStatusUpdateDto;
import org.scouts105bentaya.dto.booking.OwnBookingFormDto;
import org.scouts105bentaya.dto.booking.SimpleBookingDto;
import org.scouts105bentaya.enums.BookingDocumentStatus;
import org.scouts105bentaya.enums.ScoutCenter;
import org.scouts105bentaya.exception.BasicMessageException;
import org.scouts105bentaya.service.BookingService;
import org.scouts105bentaya.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/booking")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;
    private final BookingConverter bookingConverter;
    private final BookingDocumentConverter bookingDocumentConverter;

    public BookingController(
        BookingService bookingService,
        BookingConverter bookingConverter,
        BookingDocumentConverter bookingDocumentConverter
    ) {
        this.bookingService = bookingService;
        this.bookingConverter = bookingConverter;
        this.bookingDocumentConverter = bookingDocumentConverter;
    }

    //MANAGER

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping
    public List<BookingDto> getAll() {
        log.info("METHOD BookingController.getAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertEntityCollectionToDtoList(bookingService.findAll());
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/{id}")
    public BookingDto getById(@PathVariable Integer id) {
        log.info("METHOD BookingController.getById{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingService.findById(id));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/own/new")
    public void addOwnBooking(@RequestBody @Valid OwnBookingFormDto formDto) {
        log.info("METHOD BookingController.addOwnBooking{}", SecurityUtils.getLoggedUserUsernameForLog());
        bookingService.addOwnBooking(formDto);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PutMapping("/own/update/{id}")
    public void updateOwnBooking(@RequestBody @Valid OwnBookingFormDto formDto, @PathVariable Integer id) {
        log.info("METHOD BookingController.updatedOwnBooking --- PARAMS: id{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        bookingService.updateOwnBooking(formDto, id);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @DeleteMapping("/own/cancel/{id}")
    public BookingDto cancelOwnBooking(@PathVariable Integer id, @RequestParam String reason) {
        log.info("METHOD BookingController.cancelOwnBooking --- PARAMS: id{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingService.cancelOwnBooking(id, reason));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/dates/{center}")
    public List<BookingDateDto> getBookingForCalendar(@PathVariable ScoutCenter center) {
        log.info("METHOD BookingController.getBookingForCalendar{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingService.getBookingDates(center);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PutMapping("update-status")
    public BookingDto updateBookingStatus(@RequestBody @Valid BookingStatusUpdateDto dto) {
        log.info("METHOD BookingController.updateBookingStatus{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingService.updateStatusByManager(dto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PutMapping("/document/{id}")
    public void updateBookingDocumentStatus(@PathVariable Integer id, @RequestParam BookingDocumentStatus status) {
        log.info("METHOD BookingController.updateBookingDocumentStatus{}", SecurityUtils.getLoggedUserUsernameForLog());
        bookingService.updateBookingDocument(id, status);
    }

    //USER

    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER')")
    @GetMapping("/user")
    public List<BookingDto> getUserBookings() {
        log.info("METHOD BookingController.getUserBookings{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertEntityCollectionToDtoList(bookingService.findAllByCurrentUser());
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER')")
    @GetMapping("/user/latest")
    public BookingDto getLatestUserBookings() {
        log.info("METHOD BookingController.getLatestUserBookings{}", SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingService.findLatestByCurrentUser());
    }

    //TODO: que el usuario solo pueda subirlo si no ha confirmado los documentos, pero que no colisione con el documento
    // del estado LEFT
    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBooking(#bookingId)")
    @PostMapping(value = "/document/{bookingId}")
    public void uploadBookingDocument(@PathVariable Integer bookingId, @RequestBody MultipartFile file) {
        log.info("METHOD BookingController.uploadBookingDocument --- PARAMS bookingId: {}{}", bookingId, SecurityUtils.getLoggedUserUsernameForLog());
        bookingService.saveBookingDocument(bookingId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBooking(#dto.id)")
    @PutMapping("update-status-user")
    public BookingDto updateUserBookingStatus(@RequestBody @Valid BookingStatusUpdateDto dto) {
        log.info("METHOD BookingController.updateUserBookingStatus --- PARAMS id: {}{}", dto.getId(), SecurityUtils.getLoggedUserUsernameForLog());
        return bookingConverter.convertFromEntity(bookingService.updateStatusByUser(dto));
    }

    //BOTH

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBooking(#id)")
    @GetMapping("/document/{id}")
    public List<BookingDocumentDto> getBookingDocuments(@PathVariable Integer id) {
        log.info("METHOD BookingController.getBookingDocuments --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingDocumentConverter.convertEntityCollectionToDtoList(bookingService.findDocumentsByBookingId(id));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userOwnsBookingDocument(#documentId)")
    @GetMapping("/document/pdf/{documentId}")
    public ResponseEntity<byte[]> getBookingPdf(@PathVariable Integer documentId) {
        log.info("METHOD BookingController.getBookingPdf --- PARAMS id: {}{}", documentId, SecurityUtils.getLoggedUserUsernameForLog());
        return bookingService.getPDF(documentId);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userCanEditBookingDocument(#documentId)")
    @DeleteMapping("/document/{documentId}")
    public void deleteDocument(@PathVariable Integer documentId) {
        log.info("METHOD BookingController.deleteDocument --- PARAMS id: {}{}", documentId, SecurityUtils.getLoggedUserUsernameForLog());
        bookingService.deleteDocument(documentId);
    }

    //PUBLIC

    @GetMapping("/public/{center}")
    public List<SimpleBookingDto> getBasicBookingStatusesByCenter(@PathVariable ScoutCenter center) {
        return bookingService.getReservationDates(center);
    }

    @PostMapping("/public/check-booking")
    public List<SimpleBookingDto> getIntervalBookingStatusesByCenter(@RequestBody BookingDateFormDto dto) {
        return this.bookingService.getBookingDatesForm(dto);
    }

    @PostMapping("/public/form")
    public void saveBookingForm(@RequestBody @Valid BookingFormDto bookingFormDto) {
        log.info("METHOD BookingController.saveBookingForm");
        this.bookingService.saveFromForm(bookingFormDto);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BasicMessageException.class)
    public Map<String, String> exceptionHandler(BasicMessageException e) {
        return Map.of("message", e.getMessage());
    }
}
