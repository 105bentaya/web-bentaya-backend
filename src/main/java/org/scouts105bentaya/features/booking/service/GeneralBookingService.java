package org.scouts105bentaya.features.booking.service;

import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.features.booking.converter.BookingFormConverter;
import org.scouts105bentaya.features.booking.dto.in.BookingAcceptedDto;
import org.scouts105bentaya.features.booking.dto.in.BookingConfirmedDto;
import org.scouts105bentaya.features.booking.dto.in.BookingFormDto;
import org.scouts105bentaya.features.booking.dto.in.BookingStatusUpdateDto;
import org.scouts105bentaya.features.booking.dto.in.BookingUpdateDto;
import org.scouts105bentaya.features.booking.dto.in.BookingWarningDto;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.entity.BookingDocumentFile;
import org.scouts105bentaya.features.booking.entity.GeneralBooking;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.scouts105bentaya.features.booking.repository.GeneralBookingRepository;
import org.scouts105bentaya.features.scout_center.ScoutCenterService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.features.user.dto.UserPasswordDto;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.util.EmailUtils;
import org.scouts105bentaya.shared.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class GeneralBookingService {

    // Diagrama de estados
    // https://drive.google.com/file/d/1UxVY4xrxK12tbuVHWrrCflXzZphYJMRi/view?usp=sharing

    private final GeneralBookingRepository generalBookingRepository;
    private final TemplateEngine htmlTemplateEngine;
    private final EmailService emailService;
    private final UserService userService;
    private final ScoutCenterService scoutCenterService;
    private final BookingDocumentRepository bookingDocumentRepository;
    private final BookingDocumentService bookingDocumentService;
    private final BlobService blobService;
    private final AuthService authService;
    private final BookingFormConverter bookingFormConverter;
    private final BookingService bookingService;

    public GeneralBookingService(
        GeneralBookingRepository generalBookingRepository,
        TemplateEngine htmlTemplateEngine,
        EmailService emailService,
        UserService userService,
        ScoutCenterService scoutCenterService,
        BookingDocumentRepository bookingDocumentRepository,
        BookingDocumentService bookingDocumentService,
        BlobService blobService,
        AuthService authService,
        BookingFormConverter bookingFormConverter,
        BookingService bookingService
    ) {
        this.generalBookingRepository = generalBookingRepository;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.emailService = emailService;
        this.userService = userService;
        this.scoutCenterService = scoutCenterService;
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.bookingDocumentService = bookingDocumentService;
        this.blobService = blobService;
        this.authService = authService;
        this.bookingFormConverter = bookingFormConverter;
        this.bookingService = bookingService;
    }

    @Transactional
    public void saveFromForm(BookingFormDto dto) {
        GeneralBooking booking = bookingFormConverter.convertFromDto(dto);

        bookingService.validateBookingDates(booking);
        bookingService.validateIfBookingDateIsOpen(booking);

        booking.setStatus(BookingStatus.NEW);
        booking.setCreationDate(ZonedDateTime.now());
        booking.setExclusiveReservation(booking.isExclusiveReservation() || booking.getScoutCenter().isAlwaysExclusive());

        UserPasswordDto userPassword = this.userService.addNewScoutCenterUser(booking.getContactMail());
        booking.setUser(userPassword.user());

        GeneralBooking savedBooking = generalBookingRepository.save(booking);

        this.setBookingDocuments(savedBooking);
        this.sendNewBookingMails(savedBooking, userPassword.password());
    }

    private void setBookingDocuments(GeneralBooking booking) {
        LocalDate maxExpirationDate = booking.getEndDate().plusDays(1).toLocalDate();
        List<BookingDocument> documents = bookingDocumentRepository.findUserBookingValidDocuments(booking.getCif(), maxExpirationDate).stream()
            .map(document -> new BookingDocument()
                .setBooking(booking)
                .setType(document.getType())
                .setFile(document.getFile())
                .setStatus(document.getStatus())
                .setDuration(document.getDuration())
                .setExpirationDate(document.getExpirationDate())
            ).toList();

        bookingDocumentRepository.saveAll(documents);
    }

    private void sendNewBookingMails(GeneralBooking booking, @Nullable String newUserPassword) {
        String userSubject = "Solicitud de Reserva nº %d - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        String managementSubject = "%s - %d - Nueva Solicitud de Reserva".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, userSubject);

        context.setVariable("organization", booking.getOrganizationName());
        context.setVariable("cif", booking.getCif());
        context.setVariable("use", booking.getFacilityUse());
        context.setVariable("groupDescription", booking.getGroupDescription());
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

    public GeneralBooking bookingRejected(Integer bookingId, BookingStatusUpdateDto dto) {
        GeneralBooking currentBooking = this.getValidGeneralBooking(bookingId, null);
        currentBooking.setStatus(BookingStatus.REJECTED);
        currentBooking.setStatusObservations(dto.getObservations());
        GeneralBooking savedBooking = generalBookingRepository.save(currentBooking);
        this.sendRejectionMail(savedBooking);
        return savedBooking;
    }

    private void sendRejectionMail(GeneralBooking booking) {
        String subject = "Reserva nº %d Denegada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/rejection.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
    }

    public GeneralBooking bookingFromNewToReserved(Integer bookingId, BookingAcceptedDto dto) {
        GeneralBooking currentBooking = this.getValidGeneralBooking(bookingId, BookingStatus.NEW);
        currentBooking.setStatus(BookingStatus.RESERVED);
        currentBooking.setStatusObservations(dto.getObservations());
        currentBooking.setPrice(dto.getPrice());

        List<BookingDocument> documentsToDelete = currentBooking.getBookingDocumentList().stream()
            .filter(b -> b.getStatus() != BookingDocumentStatus.ACCEPTED).toList();

        documentsToDelete.forEach(bookingDocumentService::deleteDocument);
        currentBooking.getBookingDocumentList().removeAll(documentsToDelete);

        GeneralBooking savedBooking = generalBookingRepository.save(currentBooking);
        this.sendReservedMail(savedBooking);
        return savedBooking;
    }

    private void sendReservedMail(GeneralBooking booking) {
        String subject = "Reserva nº %d Acepada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        context.setVariable("price", booking.getPrice());
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/accepted.html", context);
        this.sendMailWithRulePdf(booking, subject, infoHtmlContent);
    }

    public GeneralBooking bookingFromReservedToOccupied(Integer bookingId, BookingConfirmedDto dto) {
        GeneralBooking currentBooking = this.getValidGeneralBooking(bookingId, BookingStatus.RESERVED);

        currentBooking.setStatus(BookingStatus.OCCUPIED);
        currentBooking.setStatusObservations(dto.getObservations());
        boolean exclusivenessRequested = currentBooking.isExclusiveReservation();
        currentBooking.setExclusiveReservation(dto.getExclusive() || currentBooking.getScoutCenter().isAlwaysExclusive());
        GeneralBooking savedBooking = generalBookingRepository.save(currentBooking);

        this.sendAcceptedMail(savedBooking, exclusivenessRequested && !currentBooking.isExclusiveReservation());

        return savedBooking;
    }

    private void sendAcceptedMail(GeneralBooking booking, boolean exclusivenessRejected) {
        String subject = "Reserva nº %d Confirmada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        context.setVariable("exclusivenessRejected", exclusivenessRejected);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/confirmed.html", context);
        this.sendMailWithRulePdf(booking, subject, infoHtmlContent);
    }

    private void sendMailWithRulePdf(GeneralBooking booking, String subject, String infoHtmlContent) {
        if (booking.getScoutCenter().getRulePdf() != null) {
            DataSource dataSource = scoutCenterService.getRulePDF(booking.getScoutCenter().getId()).asDataSource();
            this.emailService.sendSimpleEmailWithHtmlAndAttachment(subject, infoHtmlContent, dataSource, booking.getUser().getUsername());
        } else {
            log.warn("Could not find rule pdf for scout center {}", booking.getScoutCenter().getId());
            this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
        }
    }

    public GeneralBooking cancelBooking(Integer bookingId, BookingStatusUpdateDto dto) {
        GeneralBooking currentBooking = this.getValidGeneralBooking(bookingId, null);

        if (currentBooking.getStartDate().isBefore(LocalDateTime.now())) {
            log.warn("cancelBooking - can not cancel already started booking");
            throw new WebBentayaBadRequestException("No se puede cancelar una reserva iniciada");
        }

        currentBooking.setStatus(BookingStatus.CANCELED);
        currentBooking.setStatusObservations(dto.getObservations());
        GeneralBooking savedBooking = generalBookingRepository.save(currentBooking);
        this.sendCancellationMail(currentBooking);
        return savedBooking;
    }

    private void sendCancellationMail(GeneralBooking booking) {
        String userSubject = "Reserva nº %d Cancelada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        String managementSubject = "%s - %d - Reserva Cancelada".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, userSubject);
        final String userHtmlContent = this.htmlTemplateEngine.process("booking/user/cancelled.html", context);
        context.setVariable("subject", managementSubject);
        final String managementHtmlContent = this.htmlTemplateEngine.process("booking/management/cancelled.html", context);

        this.emailService.sendSimpleEmailWithHtml(userSubject, userHtmlContent, booking.getContactMail());
        this.emailService.sendSimpleEmailWithHtml(managementSubject, managementHtmlContent, getBookingEmails());
    }

    public GeneralBooking confirmDocuments(Integer bookingId) {
        GeneralBooking currentBooking = this.getValidGeneralBooking(bookingId, BookingStatus.RESERVED);
        if (!currentBooking.isUserConfirmedDocuments()) {
            currentBooking.setUserConfirmedDocuments(true);
            GeneralBooking savedBooking = generalBookingRepository.save(currentBooking);
            this.sendNotifyDocumentsUploadedMail(savedBooking);
            return savedBooking;
        }
        this.sendNotifyDocumentsUploadedMail(currentBooking);
        return currentBooking;
    }

    private void sendNotifyDocumentsUploadedMail(GeneralBooking booking) {
        String subject = "%s - %d - Documentos Disponibles".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, subject);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/management/notify-documents.html", context);
        this.emailService.sendSimpleEmailWithHtml(EmailUtils.subjectWithDateTime(subject), infoHtmlContent, getBookingEmails());
    }

    public GeneralBooking sendBookingWarning(Integer bookingId, BookingWarningDto warningDto) {
        GeneralBooking currentBooking = this.getValidGeneralBooking(bookingId, BookingStatus.RESERVED);
        currentBooking.setUserConfirmedDocuments(false);
        currentBooking.setStatusObservations(warningDto.getObservations());
        GeneralBooking savedBooking = generalBookingRepository.save(currentBooking);
        this.sendNotifyWrongDocumentsMail(currentBooking, warningDto.getSubject());
        return savedBooking;
    }

    private void sendNotifyWrongDocumentsMail(GeneralBooking booking, String subjectContent) {
        String subject = "Reserva nº %d - %s - %s".formatted(booking.getId(), subjectContent, booking.getScoutCenter().getName());

        Context context = this.getBookingBasicContext(booking, subject);

        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/user/warning.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getUser().getUsername());
    }

    public void checkForFinishedBookings() {
        List<GeneralBooking> bookingsToBeFinished = this.generalBookingRepository.findBookingsToBeFinished(LocalDateTime.now().minusHours(6));
        bookingsToBeFinished.forEach(booking -> {
            log.info("Marking booking {} as finished", booking.getId());
            booking.setFinished(true);
            generalBookingRepository.save(booking);
            this.sendFinishedEmail(booking);
        });
    }

    public void sendFinishedEmail(GeneralBooking booking) {
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

    public void saveBookingIncidencesFile(Integer bookingId, MultipartFile file) {
        FileUtils.validateFileIsDocOrPdf(file);
        GeneralBooking booking = generalBookingRepository.get(bookingId);

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
        bookingDocumentFile.setUser(authService.getLoggedUser());

        booking.setIncidencesFile(bookingDocumentFile);
        sendIncidenceFileEmail(generalBookingRepository.save(booking));
    }

    private void sendIncidenceFileEmail(GeneralBooking booking) {
        String subject = "%s - %d - Registro de Incidencias Y Estados".formatted(booking.getScoutCenter().getName(), booking.getId());
        Context context = this.getBookingBasicContext(booking, subject);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/management/incidences.html", context);
        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, getBookingEmails());
    }

    private String[] getBookingEmails() {
        return this.emailService.getSettingEmails(SettingEnum.BOOKING_MAIL);
    }

    private Context getBookingBasicContext(GeneralBooking booking, String subject) {
        Context context = bookingService.getBookingBasicContext(booking, subject);
        context.setVariable("contactMail", booking.getUser().getUsername());
        return context;
    }

    private GeneralBooking getValidGeneralBooking(Integer bookingId, @Nullable BookingStatus validStatus) {
        GeneralBooking currentBooking = generalBookingRepository.get(bookingId);
        this.validateStatus(currentBooking, validStatus);
        return currentBooking;
    }

    private void validateStatus(GeneralBooking current, @Nullable BookingStatus validStatus) {
        if ((validStatus != null && current.getStatus() != validStatus) || current.getStatus().canceledOrRejected()) {
            log.warn("validateStatus - cannot update status");
            throw new WebBentayaBadRequestException("No se puede actualizar la reserva al estado solicitado");
        }
    }

    public void updateBooking(Integer id, BookingUpdateDto dto) {
        GeneralBooking booking = this.getValidGeneralBooking(id, null);
        Map<String, Object> differences = bookingService.getBookingUpdateBasicDifferences(booking, dto);
        if (!Objects.equals(dto.groupName(), booking.getOrganizationName())) {
            booking.setOrganizationName(dto.groupName());
            differences.put("organization", booking.getOrganizationName());
        }
        if (!Objects.equals(dto.cif(), booking.getCif())) {
            booking.setCif(dto.cif());
            differences.put("cif", booking.getCif());
        }
        if (!Objects.equals(dto.price(), booking.getPrice())) {
            booking.setPrice(dto.price());
            differences.put("price", booking.getPrice());
        }

        if (!differences.isEmpty()) {
            this.bookingService.validateBookingDates(booking);
            GeneralBooking savedBooking = generalBookingRepository.save(booking);
            this.sendUpdatedBookingMail(savedBooking, differences);
        }
    }

    private void sendUpdatedBookingMail(GeneralBooking booking, Map<String, Object> differences) {
        String subject = "Cambio en Reserva nº %d - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        differences.forEach(context::setVariable);
        final String userHtmlContent = this.htmlTemplateEngine.process("booking/user/updated.html", context);
        this.emailService.sendSimpleEmailWithHtml(EmailUtils.subjectWithDateTime(subject), userHtmlContent, booking.getContactMail());
    }
}
