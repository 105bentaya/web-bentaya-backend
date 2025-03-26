package org.scouts105bentaya.features.booking.service;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingRepository;
import org.scouts105bentaya.features.scout_center.entity.ScoutCenterFile;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
public class BookingStatusService {

    private final BookingRepository bookingRepository;
    private final TemplateEngine htmlTemplateEngine;
    private final EmailService emailService;
    private final UserService userService;
    private final BlobService blobService;
    @Value("${bentaya.web.url}") private String url;

    public BookingStatusService(
        BookingRepository bookingRepository,
        TemplateEngine htmlTemplateEngine,
        EmailService emailService,
        UserService userService,
        BlobService blobService
    ) {
        this.bookingRepository = bookingRepository;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.emailService = emailService;
        this.userService = userService;
        this.blobService = blobService;
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

    public Booking bookingRejected(Booking currentBooking, String reason) {
        currentBooking.setStatus(BookingStatus.REJECTED);
        currentBooking.setStatusObservations(reason);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendRejectionMail(currentBooking);
        return savedBooking;
    }

    private void sendRejectionMail(Booking booking) {
        String subject = "Reserva nº %d Rechazada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/rejection.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
    }

    public Booking bookingFromNewToReserved(Booking currentBooking, String observations, Float price) {
        currentBooking.setStatus(BookingStatus.RESERVED);
        currentBooking.setStatusObservations(observations);
        currentBooking.setPrice(price);
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

    public Booking bookingFromReservedToOccupied(Booking currentBooking, String observations, boolean isExclusive) {
        currentBooking.setStatus(BookingStatus.OCCUPIED);
        currentBooking.setStatusObservations(observations);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendAcceptedMail(currentBooking, currentBooking.isExclusiveReservation() && !isExclusive);
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
            DataSource dataSource = this.getRulePdf(booking.getScoutCenter().getRulePdf());
            this.emailService.sendSimpleEmailWithHtmlAndAttachment(subject, infoHtmlContent, dataSource, booking.getUser().getUsername());
        } else {
            log.warn("Could not find rule pdf for scout center {}", booking.getScoutCenter().getId());
            this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
        }
    }

    public Booking bookingCanceled(Booking currentBooking, String observations) {
        currentBooking.setStatus(BookingStatus.CANCELED);
        currentBooking.setStatusObservations(observations);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendCancellationMail(currentBooking);
        return savedBooking;
    }

    private void sendCancellationMail(Booking booking) {
        String userSubject = "Reserva nº %d Cancelada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        String managementSubject = "%s - %d - Reserva Cancelada".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, userSubject);
        final String userHtmlContent = this.htmlTemplateEngine.process("booking/users/cancelled.html", context);
        context.setVariable("subject", managementSubject);
        final String managementHtmlContent = this.htmlTemplateEngine.process("booking/management/cancelled.html", context);

        this.emailService.sendSimpleEmailWithHtml(userSubject, userHtmlContent, booking.getContactMail());
        this.emailService.sendSimpleEmailWithHtml(managementSubject, managementHtmlContent, getBookingEmails());
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

    private void sendNotifyDocumentsUploadedMail(Booking booking) {
        //todo, maybe change to accept observations¿?¿?
        String subject = "%s - %d - Documentos Disponibles".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, subject);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/management/notify-documents.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, getBookingEmails());
    }

    public Booking bookingFromReservedToReservedByManager(Booking currentBooking, String observations) {
        currentBooking.setUserConfirmedDocuments(false);
        currentBooking.setStatusObservations(observations);
        Booking savedBooking = bookingRepository.save(currentBooking);
        this.sendNotifyWrongDocumentsMail(currentBooking);
        return savedBooking;
    }

    private void sendNotifyWrongDocumentsMail(Booking booking) {
        //todo cambiar frontend a aviso o similar, no 'subsanar documentos'; a lo mejor permitir siempre el botón
        String subject = "Reserva nº %d - Aviso - %s".formatted(booking.getId(), booking.getScoutCenter().getName());

        Context context = this.getBookingBasicContext(booking, subject);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/users/warning.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
    }

    private DataSource getRulePdf(ScoutCenterFile file) {
        byte[] bytes = blobService.getBlob(file.getUuid());
        ByteArrayDataSource dataSource = new ByteArrayDataSource(bytes, file.getMimeType());
        dataSource.setName(file.getName());
        return dataSource;
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
}
