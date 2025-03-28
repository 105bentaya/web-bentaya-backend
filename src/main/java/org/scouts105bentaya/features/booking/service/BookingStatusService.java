package org.scouts105bentaya.features.booking.service;

import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.dto.in.BookingAcceptedDto;
import org.scouts105bentaya.features.booking.dto.in.BookingConfirmedDto;
import org.scouts105bentaya.features.booking.dto.in.BookingStatusUpdateDto;
import org.scouts105bentaya.features.booking.dto.in.BookingWarningDto;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.scout_center.ScoutCenterService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookingStatusService {

    // Diagrama de estados
    // https://drive.google.com/file/d/1UxVY4xrxK12tbuVHWrrCflXzZphYJMRi/view?usp=sharing

    private final BookingRepository bookingRepository;
    private final TemplateEngine htmlTemplateEngine;
    private final EmailService emailService;
    private final UserService userService;
    private final ScoutCenterService scoutCenterService;
    @Value("${bentaya.web.url}") private String url;

    public BookingStatusService(
        BookingRepository bookingRepository,
        TemplateEngine htmlTemplateEngine,
        EmailService emailService,
        UserService userService,
        ScoutCenterService scoutCenterService
    ) {
        this.bookingRepository = bookingRepository;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.emailService = emailService;
        this.userService = userService;
        this.scoutCenterService = scoutCenterService;
    }

    public void saveFromForm(Booking booking) {
        booking.setStatus(BookingStatus.NEW);
        booking.setCreationDate(ZonedDateTime.now());
        booking.setExclusiveReservation(booking.isExclusiveReservation() || booking.getScoutCenter().isAlwaysExclusive());
        String password = this.userService.addNewScoutCenterUser(booking.getContactMail());
        booking.setUser(userService.findByUsername(booking.getContactMail()));
        Booking savedBooking = bookingRepository.save(booking);
        try {
            this.sendNewBookingMails(savedBooking, password);
        } catch (Exception e) {
            log.error("Error trying to send booking email: {}", e.getMessage());
        }
    }

    private void sendNewBookingMails(Booking booking, String newUserPassword) {
        String userSubject = "Solicitud de Reserva nº %d - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        String managementSubject = "%s - %d - Nueva Solicitud de Reserva".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, userSubject);

        context.setVariable("organization", booking.getOrganizationName());
        context.setVariable("cif", booking.getCif());
        context.setVariable("use", booking.getFacilityUse());
        context.setVariable("contactName", booking.getContactName());
        context.setVariable("contactRelationship", booking.getContactRelationship());
        context.setVariable("contactPhone", booking.getContactPhone());
        context.setVariable("packs", booking.getPacks());
        context.setVariable("startDate", booking.getStartDate());
        context.setVariable("endDate", booking.getEndDate());
        context.setVariable("exclusiveness", booking.isExclusiveReservation());
        context.setVariable("observations", Optional.ofNullable(booking.getObservations())
            .filter(observations -> !observations.isBlank())
            .orElse("Ninguna")
        );
        context.setVariable("newUser", newUserPassword != null);
        context.setVariable("password", newUserPassword);

        final String userHtmlContent = this.htmlTemplateEngine.process("booking/user/new-reservation.html", context);
        context.setVariable("subject", managementSubject);
        final String managementHtmlContent = this.htmlTemplateEngine.process("booking/management/new-reservation.html", context);

        this.emailService.sendSimpleEmailWithHtml(userSubject, userHtmlContent, booking.getContactMail());
        this.emailService.sendSimpleEmailWithHtml(managementSubject, managementHtmlContent, getBookingEmails());
    }

    public Booking bookingRejected(Integer bookingId, BookingStatusUpdateDto dto) {
        Booking currentBooking = this.getValidBooking(bookingId, null);
        currentBooking.setStatus(BookingStatus.REJECTED);
        currentBooking.setStatusObservations(dto.getObservations());
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendRejectionMail(currentBooking);
        return savedBooking;
    }

    private void sendRejectionMail(Booking booking) {
        String subject = "Reserva nº %d Denegada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/rejection.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
    }

    public Booking bookingFromNewToReserved(Integer bookingId, BookingAcceptedDto dto) {
        Booking currentBooking = this.getValidBooking(bookingId, BookingStatus.NEW);
        currentBooking.setStatus(BookingStatus.RESERVED);
        currentBooking.setStatusObservations(dto.getObservations());
        currentBooking.setPrice(dto.getPrice());
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendReservedMail(currentBooking);
        return savedBooking;
    }

    private void sendReservedMail(Booking booking) {
        String subject = "Reserva nº %d Acepada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        context.setVariable("price", booking.getPrice());
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/accepted.html", context);
        this.sendMailWithRulePdf(booking, subject, infoHtmlContent);
    }

    public Booking bookingFromReservedToOccupied(Integer bookingId, BookingConfirmedDto dto) {
        Booking currentBooking = this.getValidBooking(bookingId, BookingStatus.RESERVED);

        currentBooking.setStatus(BookingStatus.OCCUPIED);
        currentBooking.setStatusObservations(dto.getObservations());
        boolean exclusivenessRequested = currentBooking.isExclusiveReservation();
        currentBooking.setExclusiveReservation(dto.getExclusive() || currentBooking.getScoutCenter().isAlwaysExclusive());
        Booking savedBooking = bookingRepository.save(currentBooking);

        this.sendAcceptedMail(currentBooking, exclusivenessRequested && !currentBooking.isExclusiveReservation());
        return savedBooking;
    }

    private void sendAcceptedMail(Booking booking, boolean exclusivenessRejected) {
        String subject = "Reserva nº %d Confirmada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        context.setVariable("exclusivenessRejected", exclusivenessRejected);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/confirmed.html", context);
        this.sendMailWithRulePdf(booking, subject, infoHtmlContent);
    }

    private void sendMailWithRulePdf(Booking booking, String subject, String infoHtmlContent) {
        if (booking.getScoutCenter().getRulePdf() != null) {
            DataSource dataSource = scoutCenterService.getRulePDF(booking.getScoutCenter().getId()).asDataSource();
            this.emailService.sendSimpleEmailWithHtmlAndAttachment(subject, infoHtmlContent, dataSource, booking.getUser().getUsername());
        } else {
            log.warn("Could not find rule pdf for scout center {}", booking.getScoutCenter().getId());
            this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
        }
    }

    public Booking cancelBooking(Integer bookingId, BookingStatusUpdateDto dto) {
        Booking currentBooking = this.getValidBooking(bookingId, null);

        if (currentBooking.getStartDate().isBefore(LocalDateTime.now())) {
            log.warn("cancelBooking - can not cancel already started booking");
            throw new WebBentayaBadRequestException("No se puede cancelar una reserva iniciada");
        }

        currentBooking.setStatus(BookingStatus.CANCELED);
        currentBooking.setStatusObservations(dto.getObservations());
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendCancellationMail(currentBooking);
        return savedBooking;
    }

    private void sendCancellationMail(Booking booking) {
        String userSubject = "Reserva nº %d Cancelada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        String managementSubject = "%s - %d - Reserva Cancelada".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, userSubject);
        final String userHtmlContent = this.htmlTemplateEngine.process("booking/user/cancelled.html", context);
        context.setVariable("subject", managementSubject);
        final String managementHtmlContent = this.htmlTemplateEngine.process("booking/management/cancelled.html", context);

        this.emailService.sendSimpleEmailWithHtml(userSubject, userHtmlContent, booking.getContactMail());
        this.emailService.sendSimpleEmailWithHtml(managementSubject, managementHtmlContent, getBookingEmails());
    }

    public Booking confirmDocuments(Integer bookingId) {
        Booking currentBooking = this.getValidBooking(bookingId, BookingStatus.RESERVED);
        if (!currentBooking.isUserConfirmedDocuments()) {
            currentBooking.setUserConfirmedDocuments(true);
            Booking savedBooking = bookingRepository.save(currentBooking);
            this.sendNotifyDocumentsUploadedMail(savedBooking);
            return savedBooking;
        }
        this.sendNotifyDocumentsUploadedMail(currentBooking);
        return currentBooking;
    }

    private void sendNotifyDocumentsUploadedMail(Booking booking) {
        String subject = "%s - %d - Documentos Disponibles".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, subject);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/management/notify-documents.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, getBookingEmails());
    }

    public Booking sendBookingWarning(Integer bookingId, BookingWarningDto warningDto) {
        Booking currentBooking = this.getValidBooking(bookingId, BookingStatus.RESERVED);
        currentBooking.setUserConfirmedDocuments(false);
        currentBooking.setStatusObservations(warningDto.getObservations());
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendNotifyWrongDocumentsMail(currentBooking, warningDto.getSubject());
        return savedBooking;
    }

    private void sendNotifyWrongDocumentsMail(Booking booking, String subjectContent) {
        String subject = "Reserva nº %d - %s - %s".formatted(booking.getId(), subjectContent, booking.getScoutCenter().getName());

        Context context = this.getBookingBasicContext(booking, subject);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/warning.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
    }

    public void checkForFinishedBookings() {
        List<Booking> bookingsToBeFinished = this.bookingRepository.findBookingsToBeFinished(LocalDateTime.now().minusHours(6));
        bookingsToBeFinished.forEach(booking -> {
            log.info("Setting booking {} as finished", booking.getId());
            booking.setFinished(true);
            bookingRepository.save(booking);
            this.sendFinishedEmail(booking);
        });
    }

    public void sendFinishedEmail(Booking booking) {
        String subject = "Reserva nº %d Finalizada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/finished.html", context);

        if (booking.getScoutCenter().getIncidencesDoc() != null) {
            this.emailService.sendSimpleEmailWithHtmlAndAttachment(
                subject,
                infoHtmlContent,
                scoutCenterService.getIncidenceFile(booking.getScoutCenter().getId()).asDataSource(),
                booking.getUser().getUsername()
            );
        } else {
            log.warn("Could not find incidence file for scout center {}", booking.getScoutCenter().getId());
            this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
        }
    }

    private String[] getBookingEmails() {
        return this.emailService.getSettingEmails(SettingEnum.BOOKING_MAIL);
    }

    private Context getBookingBasicContext(Booking booking, String subject) {
        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());
        context.setVariable("contactMail", booking.getUser().getUsername());
        context.setVariable("bookingInformationMail", this.getBookingEmails()[0]);
        context.setVariable("webUrl", url);
        context.setVariable("subject", subject);
        context.setVariable("color", "#ED5565");

        return context;
    }

    private Booking getValidBooking(Integer bookingId, @Nullable BookingStatus validStatus) {
        Booking currentBooking = bookingRepository.findById(bookingId).orElseThrow(WebBentayaNotFoundException::new);
        this.validateBookingOwnership(currentBooking);
        this.validateStatus(currentBooking, validStatus);
        return currentBooking;
    }

    private void validateStatus(Booking current, @Nullable BookingStatus validStatus) {
        if ((validStatus != null && current.getStatus() != validStatus) || current.getStatus().canceledOrRejected()) {
            log.warn("validateStatus - cannot update status");
            throw new WebBentayaBadRequestException("No se puede actualizar la reserva al estado solicitado");
        }
    }

    private void validateBookingOwnership(Booking booking) {
        if (booking.isOwnBooking()) {
            log.warn("validateBookingOwnership - booking is own booking");
            throw new WebBentayaBadRequestException("No se puede actualizar el estado a una reserva de la asociación");
        }
    }
}
