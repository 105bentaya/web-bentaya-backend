package org.scouts105bentaya.features.booking.service;

import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.features.booking.dto.in.BookingConfirmedDto;
import org.scouts105bentaya.features.booking.dto.in.BookingStatusUpdateDto;
import org.scouts105bentaya.features.booking.dto.in.BookingUpdateDto;
import org.scouts105bentaya.features.booking.dto.in.OwnBookingFormDto;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.OwnBookingRepository;
import org.scouts105bentaya.features.booking.specification.BookingSpecificationFilter;
import org.scouts105bentaya.features.booking.specification.OwnBookingSpecification;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.group.GroupService;
import org.scouts105bentaya.features.scout_center.ScoutCenterService;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.util.EmailUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class OwnBookingService {

    private final ScoutCenterRepository scoutCenterRepository;
    private final BookingService bookingService;
    private final OwnBookingRepository ownBookingRepository;
    private final GroupService groupService;
    private final TemplateEngine htmlTemplateEngine;
    private final EmailService emailService;
    private final ScoutCenterService scoutCenterService;

    public OwnBookingService(
        ScoutCenterRepository scoutCenterRepository,
        BookingService bookingService,
        OwnBookingRepository ownBookingRepository,
        GroupService groupService,
        TemplateEngine htmlTemplateEngine,
        EmailService emailService,
        ScoutCenterService scoutCenterService
    ) {
        this.scoutCenterRepository = scoutCenterRepository;
        this.bookingService = bookingService;
        this.ownBookingRepository = ownBookingRepository;
        this.groupService = groupService;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.emailService = emailService;
        this.scoutCenterService = scoutCenterService;
    }

    public Page<OwnBooking> findAll(BookingSpecificationFilter filter) {
        return ownBookingRepository.findAll(new OwnBookingSpecification(filter), filter.getPageable());
    }

    public void addOwnBooking(OwnBookingFormDto dto) {
        OwnBooking booking = new OwnBooking();
        booking.setPacks(dto.packs());
        booking.setStartDate(dto.startDate());
        booking.setEndDate(dto.endDate());
        booking.setScoutCenter(scoutCenterRepository.get(dto.scoutCenterId()));
        booking.setPacks(dto.packs());
        booking.setObservations(dto.observations());
        booking.setExclusiveReservation(booking.getScoutCenter().isAlwaysExclusive() || dto.exclusiveReservation());
        booking.setGroup(dto.groupId() == 0 ? null : groupService.findById(dto.groupId()));

        bookingService.validateBookingDates(booking);

        booking.setStatus(BookingStatus.RESERVED);
        booking.setCreationDate(ZonedDateTime.now());

        OwnBooking savedBooking = ownBookingRepository.save(booking);
        this.sendNewOwnBookingMail(savedBooking);
    }

    private void sendNewOwnBookingMail(OwnBooking booking) {
        String managementSubject = "%s - %d - Nueva Solicitud de Reserva".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, managementSubject);
        context.setVariable("startDate", booking.getStartDate());
        context.setVariable("endDate", booking.getEndDate());
        context.setVariable("observations", booking.getObservations());
        final String managementHtmlContent = this.htmlTemplateEngine.process("booking/management/new-own-reservation.html", context);
        emailService.sendSimpleEmailWithHtml(managementSubject, managementHtmlContent, getBookingEmails());
    }

    public OwnBooking cancelOwnBooking(Integer bookingId, BookingStatusUpdateDto dto) {
        OwnBooking currentBooking = this.getValidGeneralBooking(bookingId, null);

        if (currentBooking.getStartDate().isBefore(LocalDateTime.now())) {
            log.warn("cancelBooking - can not cancel already started booking");
            throw new WebBentayaBadRequestException("No se puede cancelar una reserva iniciada");
        }

        currentBooking.setStatus(BookingStatus.CANCELED);
        currentBooking.setStatusObservations(dto.getObservations());
        OwnBooking savedBooking = ownBookingRepository.save(currentBooking);
        this.sendCancellationMail(currentBooking);
        return savedBooking;
    }

    private void sendCancellationMail(OwnBooking booking) {
        String managementSubject = "%s - %d - Reserva Cancelada".formatted(booking.getScoutCenter().getName(), booking.getId());

        Context context = this.getBookingBasicContext(booking, managementSubject);
        final String managementHtmlContent = this.htmlTemplateEngine.process("booking/management/own-cancelled.html", context);
        this.emailService.sendSimpleEmailWithHtml(managementSubject, managementHtmlContent, getBookingEmails());
    }


    public OwnBooking confirmOwnBooking(Integer bookingId, BookingConfirmedDto dto) {
        OwnBooking currentBooking = this.getValidGeneralBooking(bookingId, BookingStatus.RESERVED);
        currentBooking.setStatus(BookingStatus.OCCUPIED);
        currentBooking.setStatusObservations(dto.getObservations());
        boolean exclusivenessRequested = currentBooking.isExclusiveReservation();
        currentBooking.setExclusiveReservation(dto.getExclusive() || currentBooking.getScoutCenter().isAlwaysExclusive());
        OwnBooking savedBooking = ownBookingRepository.save(currentBooking);

        if (savedBooking.getGroup() != null) {
            this.sendConfirmedMail(savedBooking, exclusivenessRequested && !currentBooking.isExclusiveReservation());
        }

        return savedBooking;
    }

    private void sendConfirmedMail(OwnBooking booking, boolean exclusivenessRejected) {
        String subject = "Reserva nº %d Confirmada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        context.setVariable("exclusivenessRejected", exclusivenessRejected);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/group/confirmed.html", context);
        this.sendMailWithRulePdf(booking, subject, infoHtmlContent);
    }

    private void sendMailWithRulePdf(OwnBooking booking, String subject, String infoHtmlContent) {
        if (booking.getScoutCenter().getRulePdf() != null) {
            DataSource dataSource = scoutCenterService.getRulePDF(booking.getScoutCenter().getId()).asDataSource();
            this.emailService.sendSimpleEmailWithHtmlAndAttachment(subject, infoHtmlContent, dataSource, booking.getGroup().getEmail());
        } else {
            log.warn("Could not find rule pdf for scout center {}", booking.getScoutCenter().getId());
            this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getGroup().getEmail());
        }
    }

    public OwnBooking rejectOwnBooking(Integer bookingId, BookingStatusUpdateDto dto) {
        OwnBooking currentBooking = this.getValidGeneralBooking(bookingId, null);
        currentBooking.setStatus(BookingStatus.REJECTED);
        currentBooking.setStatusObservations(dto.getObservations());
        OwnBooking savedBooking = ownBookingRepository.save(currentBooking);
        if (savedBooking.getGroup() != null) {
            this.sendRejectionMail(savedBooking);
        }
        return savedBooking;
    }

    private void sendRejectionMail(OwnBooking booking) {
        String subject = "Reserva nº %d Denegada - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        final String infoHtmlContent = this.htmlTemplateEngine.process("booking/group/rejection.html", context);

        this.emailService.sendSimpleEmailWithHtml(subject, infoHtmlContent, booking.getGroup().getEmail());
    }


    private OwnBooking getValidGeneralBooking(Integer bookingId, @Nullable BookingStatus validStatus) {
        OwnBooking currentBooking = ownBookingRepository.get(bookingId);
        this.validateStatus(currentBooking, validStatus);
        return currentBooking;
    }

    private void validateStatus(OwnBooking current, @Nullable BookingStatus validStatus) {
        if ((validStatus != null && current.getStatus() != validStatus) || current.getStatus().canceledOrRejected()) {
            log.warn("validateStatus - cannot update status");
            throw new WebBentayaBadRequestException("No se puede actualizar la reserva al estado solicitado");
        }
    }


    private Context getBookingBasicContext(OwnBooking booking, String subject) {
        Context context = bookingService.getBookingBasicContext(booking, subject);

        Optional<Group> group = Optional.ofNullable(booking.getGroup());
        context.setVariable("contactMail", group.map(Group::getEmail).orElse(null));
        context.setVariable("groupName", group.map(Group::getName).orElse("Grupo"));
        return context;
    }

    private String[] getBookingEmails() {
        return emailService.getSettingEmails(SettingEnum.BOOKING_MAIL);
    }

    public void updateBooking(Integer id, @Valid BookingUpdateDto dto) {
        OwnBooking booking = this.getValidGeneralBooking(id, null);
        Map<String, Object> differences = bookingService.getBookingUpdateBasicDifferences(booking, dto);
        if (!differences.isEmpty()) {
            this.bookingService.validateBookingDates(booking);
            OwnBooking savedBooking = ownBookingRepository.save(booking);
            if (savedBooking.getGroup() != null) {
                this.sendUpdatedBookingMail(savedBooking, differences);
            }
        }
    }

    private void sendUpdatedBookingMail(OwnBooking booking, Map<String, Object> differences) {
        String subject = "Cambio en Reserva nº %d - %s".formatted(booking.getId(), booking.getScoutCenter().getName());
        Context context = this.getBookingBasicContext(booking, subject);
        differences.forEach(context::setVariable);
        final String userHtmlContent = this.htmlTemplateEngine.process("booking/group/updated.html", context);
        this.emailService.sendSimpleEmailWithHtml(EmailUtils.subjectWithDateTime(subject), userHtmlContent, booking.getGroup().getEmail());
    }

}
