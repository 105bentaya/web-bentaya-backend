package org.scouts105bentaya.features.booking.service;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
public class BookingStatusService {

    private final BookingRepository bookingRepository;
    private final TemplateEngine htmlTemplateEngine;
    private final EmailService emailService;
    private final UserService userService;
    @Value("${bentaya.email.booking}") private String bookingMail;
    @Value("${bentaya.web.url}") private String url;

    public BookingStatusService(
        BookingRepository bookingRepository,
        TemplateEngine htmlTemplateEngine,
        EmailService emailService,
        UserService userService
    ) {
        this.bookingRepository = bookingRepository;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.emailService = emailService;
        this.userService = userService;
    }

    public void saveFromForm(Booking booking) {
        booking.setStatus(BookingStatus.NEW);
        booking.setCreationDate(ZonedDateTime.now());
        String password = this.userService.addNewScoutCenterUser(booking.getContactMail());
        booking.setUser(userService.findByUsername(booking.getContactMail()));
        Booking savedBooking = bookingRepository.save(booking);
        try {
            this.sendNewBookingMails(savedBooking, password);
        } catch (Exception e) {
            log.error("Error trying to send booking email: {}", e.getMessage());
        }
    }

    public Booking bookingFromNewToRejected(Booking currentBooking, String reason) {
        currentBooking.setStatus(BookingStatus.REJECTED);
        currentBooking.setStatusObservations(reason);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendRejectionMail(currentBooking);
        return savedBooking;
    }

    public Booking bookingFromNewToReserved(Booking currentBooking, String observations, Float price) {
        currentBooking.setStatus(BookingStatus.RESERVED);
        currentBooking.setStatusObservations(observations);
        currentBooking.setPrice(price);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendReservedMail(currentBooking);
        return savedBooking;
    }

    public Booking bookingCanceled(Booking currentBooking, String observations) {
        currentBooking.setStatus(BookingStatus.CANCELED);
        currentBooking.setStatusObservations(observations);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendCancellationMail(currentBooking);
        return savedBooking;
    }

    public Booking bookingFromReservedToReservedByUser(Booking currentBooking) {
        if (!currentBooking.isUserConfirmedDocuments()) {
            currentBooking.setUserConfirmedDocuments(true);
            Booking savedBooking = bookingRepository.save(currentBooking);
            this.sendNotifyDocumentsUploadedMail(savedBooking);
            return savedBooking;
        }
        this.sendNotifyDocumentsUploadedMail(currentBooking);
        return currentBooking;
    }

    public Booking bookingFromReservedToReservedByManager(Booking currentBooking, String observations) {
        currentBooking.setUserConfirmedDocuments(false);
        currentBooking.setStatusObservations(observations);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendNotifyWrongDocumentsMail(currentBooking);
        return savedBooking;
    }

    public Booking bookingFromReservedToOccupied(Booking currentBooking, String observations, boolean fully) {
        currentBooking.setStatus(fully ? BookingStatus.FULLY_OCCUPIED : BookingStatus.OCCUPIED);
        currentBooking.setStatusObservations(observations);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendAcceptedMail(currentBooking, currentBooking.isExclusiveReservation() && !fully);
        return savedBooking;
    }

    //TODO: el string documents habría que cambiarlo por un nuevo campo boolean
    public Booking bookingFromOccupiedToLeftByUser(Booking currentBooking, String documents) {
        currentBooking.setStatus(BookingStatus.LEFT);
        currentBooking.setUserConfirmedDocuments(documents != null && !documents.isBlank());
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendLeftMail(currentBooking);
        return savedBooking;
    }

    public Booking bookingToFinished(Booking currentBooking, String observations) {
        currentBooking.setStatus(BookingStatus.FINISHED);
        currentBooking.setStatusObservations(observations);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendFinishedMail(currentBooking);
        return savedBooking;
    }

    private void sendFinishedMail(Booking booking) {
        String userMail = booking.getUser().getUsername();

        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());
        context.setVariable("contactMail", userMail);
        context.setVariable("url", url);

        // todo: que el link te lleva al formulario de reservas. Si no está iniciada la sesión, que te lleve al portal de inicio de sesión y después vuelvas al formulario (usar query param)
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-finished.html", context);
        String subject = String.format(
            "Reserva de Centros Scout Nº %d Revisada",
            booking.getId()
        );

        this.emailService.sendSimpleEmailWithHtml(userMail, subject, infoHtmlContent);
    }

    private void sendLeftMail(Booking booking) {
        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("uploadedDocument", booking.isUserConfirmedDocuments());

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-left.html", context);
        String subject = String.format(
            "Centro Scout %s - Finalizada la reserva Nº %d ",
            booking.getScoutCenter().getName(), booking.getId()
        );

        this.emailService.sendSimpleEmailWithHtml(bookingMail, subject, infoHtmlContent);
    }

    private void sendAcceptedMail(Booking booking, boolean exclusivenessRejected) {
        String userMail = booking.getUser().getUsername();

        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());
        context.setVariable("contactMail", userMail);
        context.setVariable("exclusivenessRejected", exclusivenessRejected);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-accepted.html", context);
        String subject = String.format(
            "Reserva de Centros Scout Nº %d Confirmada",
            booking.getId()
        );
        try {
            DataSource dataSource = this.generatePdfDataSource(booking.getScoutCenter().getPdfName());
            this.emailService.sendSimpleEmailWithHtmlAndAttachment(userMail, subject, infoHtmlContent, dataSource);
        } catch (IOException e) {
            log.error("Error trying to fetch pdf: {}", e.getMessage());
            this.emailService.sendSimpleEmailWithHtml(userMail, subject, infoHtmlContent);
        }
    }

    private void sendNotifyWrongDocumentsMail(Booking booking) {
        String userMail = booking.getUser().getUsername();

        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());
        context.setVariable("contactMail", userMail);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-notify-documents-wrong.html", context);
        String subject = String.format(
            "Reserva de Centros Scout Nº %d - Documentos rechazados",
            booking.getId()
        );
        this.emailService.sendSimpleEmailWithHtml(userMail, subject, infoHtmlContent);
    }

    private void sendNotifyDocumentsUploadedMail(Booking booking) {
        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-notify-documents-uploaded.html", context);
        String subject = String.format(
            "Centro Scout %s - Documentos disponibles para reserva Nº %d",
            booking.getScoutCenter().getName(),
            booking.getId()
        );
        this.emailService.sendSimpleEmailWithHtml(bookingMail, subject, infoHtmlContent);
    }

    private void sendCancellationMail(Booking booking) {
        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-cancellation.html", context);
        String subject = String.format("Centro Scout %s - Reserva Nº %d cancelada", booking.getScoutCenter().getName(), booking.getId());
        this.emailService.sendSimpleEmailWithHtml(bookingMail, subject, infoHtmlContent);
    }

    private void sendReservedMail(Booking booking) {
        String userMail = booking.getUser().getUsername();

        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());
        context.setVariable("contactMail", userMail);
        context.setVariable("price", booking.getPrice());

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-reservation-accepted.html", context);
        try {
            DataSource dataSource = this.generatePdfDataSource(booking.getScoutCenter().getPdfName());
            this.emailService.sendSimpleEmailWithHtmlAndAttachment(userMail, "Reserva de Centro Scout Aceptada", infoHtmlContent, dataSource);
        } catch (IOException e) {
            log.error("Error trying to fetch pdf: {}", e.getMessage());
            this.emailService.sendSimpleEmailWithHtml(userMail, "Reserva de Centro Scout Aceptada", infoHtmlContent);
        }
    }

    private void sendRejectionMail(Booking booking) {
        String userMail = booking.getUser().getUsername();

        Context context = new Context();
        context.setVariable("id", booking.getId());
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("statusObservation", booking.getStatusObservations());
        context.setVariable("contactMail", userMail);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-rejection.html", context);
        this.emailService.sendSimpleEmailWithHtml(userMail, "Reserva de Centro Scout Rechazada", infoHtmlContent);
    }

    private void sendNewBookingMails(Booking booking, String newUserPassword) {
        Context context = new Context();
        context.setVariable("center", booking.getScoutCenter().getName());
        context.setVariable("organization", booking.getOrganizationName());
        context.setVariable("cif", booking.getCif());
        context.setVariable("use", booking.getFacilityUse());
        context.setVariable("contactName", booking.getContactName());
        context.setVariable("contactRelationship", booking.getContactRelationship());
        context.setVariable("contactMail", booking.getContactMail());
        context.setVariable("contactPhone", booking.getContactPhone());
        context.setVariable("id", booking.getId());
        context.setVariable("packs", booking.getPacks());
        context.setVariable("startDate", booking.getStartDate());
        context.setVariable("endDate", booking.getEndDate());
        context.setVariable("exclusiveness", booking.isExclusiveReservation());
        context.setVariable("observations", Optional.ofNullable(booking.getObservations())
            .filter(observations -> !observations.isBlank())
            .orElse("Ninguna")
        );

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking-new-reservation.html", context);

        context.setVariable("newUser", newUserPassword != null);
        context.setVariable("password", newUserPassword);

        final String userHtmlContent = this.htmlTemplateEngine.process("booking-new-reservation.html", context);

        String subject = String.format("Centro Scout %s - Nueva Reserva", booking.getScoutCenter().getName());
        this.emailService.sendSimpleEmailWithHtml(bookingMail, subject, infoHtmlContent);
        this.emailService.sendSimpleEmailWithHtml(booking.getContactMail(), "Centro Scout - Nueva Reserva", userHtmlContent);
    }

    private DataSource generatePdfDataSource(String pdfName) throws IOException {
        byte[] bytes = new ClassPathResource("documents/" + pdfName).getContentAsByteArray();
        ByteArrayDataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
        dataSource.setName(pdfName);
        return dataSource;
    }
}
