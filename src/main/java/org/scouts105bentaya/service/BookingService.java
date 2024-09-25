package org.scouts105bentaya.service;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookingService {

    List<Booking> findAll();

    List<Booking> findAllByCurrentUser();

    Booking findById(Integer id);

    List<BookingDocument> findDocumentsByBookingId(Integer id);

    Booking updateStatusByManager(BookingStatusUpdateDto newStatusDto);

    Booking updateStatusByUser(BookingStatusUpdateDto newStatusDto);

    List<SimpleBookingDto> getReservationDates(ScoutCenter scoutCenter);

    List<BookingDateDto> getBookingDates(ScoutCenter scoutCenter);

    void addOwnBooking(OwnBookingFormDto dto);

    void updateOwnBooking(OwnBookingFormDto formDto, Integer id);

    Booking cancelOwnBooking(Integer id, String reason);

    void saveFromForm(BookingFormDto dto);

    List<SimpleBookingDto> getBookingDatesForm(BookingDateFormDto dto);

    void saveBookingDocument(Integer bookingId, MultipartFile file);

    void updateBookingDocument(Integer id, BookingDocumentStatus status);

    void deleteDocument(Integer id);

    ResponseEntity<byte[]> getPDF(Integer id);

    Booking findLatestByCurrentUser();
}
