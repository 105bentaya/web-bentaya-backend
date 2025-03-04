package org.scouts105bentaya.features.event.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.EventRepository;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.scout.ScoutService;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.Group;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;

    private final ScoutService scoutService;
    private final ConfirmationService confirmationService;

    public EventService(
        EventRepository eventRepository,
        ScoutService scoutService,
        ConfirmationService confirmationService
    ) {
        this.eventRepository = eventRepository;
        this.scoutService = scoutService;
        this.confirmationService = confirmationService;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public List<Event> findAllByGroupId(Group id) {
        return eventRepository.findAllByGroupId(id);
    }

    public List<Event> findAllByGroupIdAndActivatedAttendance(Group id) {
        return eventRepository.findAllByGroupIdAndActiveAttendanceListIsTrue(id);
    }

    public Event findById(Integer id) {
        return eventRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    @Transactional
    public Event save(EventFormDto eventForm) {
        Event newEvent = new Event();
        newEvent.setTitle(eventForm.title());
        newEvent.setGroupId(Group.valueOf(eventForm.groupId()));
        newEvent.setDescription(eventForm.description());
        newEvent.setLocation(eventForm.location());

        if (newEvent.getGroupId().isNotUnit() && eventForm.activateAttendanceList()) {
            throw new WebBentayaBadRequestException("No se puede activar la asistencia para esta unidad");
        }

        newEvent.setActiveAttendanceList(eventForm.activateAttendanceList());
        newEvent.setActiveAttendancePayment(eventForm.activateAttendanceList() && eventForm.activateAttendancePayment());
        newEvent.setClosedAttendanceList(eventForm.closeAttendanceList());
        newEvent.setCloseDateTime(eventForm.closeAttendanceList() ? null : eventForm.closeDateTime());

        this.setEventCoordinates(newEvent, eventForm);
        this.setEventDates(newEvent, eventForm);

        Event savedEvent = eventRepository.save(newEvent);
        if (savedEvent.isActiveAttendanceList()) {
            this.scoutService.findAllByLoggedScouterGroupId().forEach(scout -> {
                Confirmation confirmation = new Confirmation();
                confirmation.setEvent(savedEvent);
                confirmation.setScout(scout);
                this.confirmationService.save(confirmation);
            });
        }
        return savedEvent;
    }

    @Transactional
    public Event update(EventFormDto eventForm) {
        Event eventDB = findById(eventForm.id());

        eventDB.setTitle(eventForm.title());
        eventDB.setGroupId(Group.valueOf(eventForm.groupId()));
        eventDB.setDescription(eventForm.description());
        eventDB.setLocation(eventForm.location());

        this.setEventCoordinates(eventDB, eventForm);
        this.setEventDates(eventDB, eventForm);
        this.updateEventAttendance(eventForm, eventDB);

        return eventRepository.save(eventDB);
    }

    private void setEventDates(Event event, EventFormDto eventForm) {
        event.setUnknownTime(eventForm.unknownTime());
        if (eventForm.unknownTime()) {
            if (eventForm.localStartDate() == null || eventForm.localEndDate() == null) {
                log.warn("setEventDates - local start {} or end dates {} invalid", eventForm.localStartDate(), eventForm.localEndDate());
                throw new WebBentayaBadRequestException("Fechas no especificadas");
            }
            event.setStartDate(ZonedDateTime.of(eventForm.localStartDate(), LocalTime.of(11, 0), GenericConstants.UTC_ZONE));
            event.setEndDate(ZonedDateTime.of(eventForm.localEndDate(), LocalTime.of(13, 0), GenericConstants.UTC_ZONE));
        } else {
            if (eventForm.startDate() == null || eventForm.endDate() == null) {
                log.warn("setEventDates - start {} or end dates {} invalid", eventForm.startDate(), eventForm.startDate());
                throw new WebBentayaBadRequestException("Fechas no especificadas");
            }
            event.setEndDate(eventForm.endDate().truncatedTo(ChronoUnit.MINUTES));
            event.setStartDate(eventForm.startDate().truncatedTo(ChronoUnit.MINUTES));
        }
        if (!event.getStartDate().isBefore(event.getEndDate())) {
            log.warn("setEventDates - start date {} is not before end date {}", event.getStartDate(), event.getEndDate());
            throw new WebBentayaBadRequestException("La fecha de fin no debe ser anterior a la de inicio");
        }
    }

    private void setEventCoordinates(Event event, EventFormDto eventForm) {
        if (eventForm.latitude() == null || eventForm.longitude() == null) {
            event.setLatitude(null);
            event.setLongitude(null);
        } else {
            event.setLatitude(eventForm.latitude());
            event.setLongitude(eventForm.longitude());
        }
    }

    private void updateEventAttendance(EventFormDto eventForm, Event eventDB) {
        if (!eventForm.activateAttendanceList() || eventDB.getGroupId().isNotUnit()) {
            eventDB.setActiveAttendanceList(false);
            eventDB.setActiveAttendancePayment(false);
            eventDB.setClosedAttendanceList(false);
            eventDB.setCloseDateTime(null);
            confirmationService.deleteAllByEventId(eventDB.getId());
        } else if (!eventDB.isActiveAttendanceList()) {
            eventDB.setActiveAttendanceList(true);
            eventDB.setActiveAttendancePayment(eventForm.activateAttendancePayment());
            eventDB.setClosedAttendanceList(eventDB.isClosedAttendanceList());
            eventDB.setCloseDateTime(eventDB.getCloseDateTime());
            this.scoutService.findAllByLoggedScouterGroupId().forEach(scout -> {
                Confirmation confirmation = new Confirmation();
                confirmation.setEvent(eventDB);
                confirmation.setScout(scout);
                if (eventForm.activateAttendancePayment()) confirmation.setPayed(false);
                confirmationService.save(confirmation);
            });
        } else {
            eventDB.setClosedAttendanceList(eventForm.closeAttendanceList());
            eventDB.setCloseDateTime(eventForm.closeDateTime());
            if (!eventDB.isActiveAttendancePayment() && eventForm.activateAttendancePayment()) {
                eventDB.setActiveAttendancePayment(true);
                eventDB.getConfirmationList().forEach(confirmation -> {
                    confirmation.setPayed(false);
                    confirmationService.save(confirmation);
                });
            } else if (eventDB.isActiveAttendancePayment() && !eventForm.activateAttendancePayment()) {
                eventDB.setActiveAttendancePayment(false);
                eventDB.getConfirmationList().stream().filter(confirmation -> confirmation.getPayed() != null).forEach(confirmation -> {
                    confirmation.setPayed(null);
                    confirmationService.save(confirmation);
                });
            }
        }
    }

    public void delete(Integer id) {
        eventRepository.deleteById(id);
    }
}
