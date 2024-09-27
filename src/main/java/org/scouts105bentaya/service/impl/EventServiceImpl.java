package org.scouts105bentaya.service.impl;

import org.scouts105bentaya.constant.GenericConstants;
import org.scouts105bentaya.dto.event.EventFormDto;
import org.scouts105bentaya.entity.Confirmation;
import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.enums.Group;
import org.scouts105bentaya.exception.EventNotFoundException;
import org.scouts105bentaya.exception.WebBentayaException;
import org.scouts105bentaya.repository.EventRepository;
import org.scouts105bentaya.service.ConfirmationService;
import org.scouts105bentaya.service.EventService;
import org.scouts105bentaya.service.ScoutService;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final ScoutService scoutService;
    private final ConfirmationService confirmationService;

    public EventServiceImpl(
        EventRepository eventRepository,
        ScoutService scoutService,
        ConfirmationService confirmationService
    ) {
        this.eventRepository = eventRepository;
        this.scoutService = scoutService;
        this.confirmationService = confirmationService;
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> findAllByGroupId(Group id) {
        return eventRepository.findAllByGroupId(id);
    }

    @Override
    public List<Event> findAllByGroupIdAndActivatedAttendance(Group id) {
        return eventRepository.findAllByGroupIdAndActiveAttendanceListIsTrue(id);
    }

    @Override
    public Event findById(Integer id) {
        return eventRepository.findById(id).orElseThrow(EventNotFoundException::new);
    }

    @Override
    @Transient
    public Event save(EventFormDto eventForm) {
        Event newEvent = new Event();
        newEvent.setTitle(eventForm.getTitle());
        newEvent.setGroupId(Group.valueOf(eventForm.getGroupId()));
        newEvent.setDescription(eventForm.getDescription());
        newEvent.setLocation(eventForm.getLocation());

        if (newEvent.getGroupId().isNotUnit() && eventForm.isActivateAttendanceList()) throw new WebBentayaException("No se puede activar la asistencia para esta unidad");
        newEvent.setActiveAttendanceList(eventForm.isActivateAttendanceList());
        if (eventForm.isActivateAttendanceList()) newEvent.setActiveAttendancePayment(eventForm.isActivateAttendancePayment());

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

    @Override
    @Transient
    public Event update(EventFormDto eventForm) {
        Event eventDB = findById(eventForm.getId());

        eventDB.setTitle(eventForm.getTitle());
        eventDB.setGroupId(Group.valueOf(eventForm.getGroupId()));
        eventDB.setDescription(eventForm.getDescription());
        eventDB.setLocation(eventForm.getLocation());

        this.setEventCoordinates(eventDB, eventForm);
        this.setEventDates(eventDB, eventForm);
        this.updateEventAttendance(eventForm, eventDB);

        return eventRepository.save(eventDB);
    }

    private void setEventDates(Event event, EventFormDto eventForm) {
        event.setUnknownTime(eventForm.isUnknownTime());
        if (eventForm.isUnknownTime()) {
            if (eventForm.getLocalStartDate() == null || eventForm.getLocalEndDate() == null)
                throw new WebBentayaException("Fechas no especificadas");
            event.setStartDate(ZonedDateTime.of(eventForm.getLocalStartDate(), LocalTime.of(11, 0), GenericConstants.UTC_ZONE));
            event.setEndDate(ZonedDateTime.of(eventForm.getLocalEndDate(), LocalTime.of(13, 0), GenericConstants.UTC_ZONE));
        } else {
            if (eventForm.getStartDate() == null || eventForm.getEndDate() == null)
                throw new WebBentayaException("Fechas no especificadas");
            event.setEndDate(eventForm.getEndDate().truncatedTo(ChronoUnit.MINUTES));
            event.setStartDate(eventForm.getStartDate().truncatedTo(ChronoUnit.MINUTES));
        }
        if (!event.getStartDate().isBefore(event.getEndDate()))
            throw new WebBentayaException("La fecha de fin no debe ser anterior a la de inicio");
    }

    private void setEventCoordinates(Event event, EventFormDto eventForm) {
        if (eventForm.getLatitude() == null || eventForm.getLongitude() == null) {
            event.setLatitude(null);
            event.setLongitude(null);
        } else {
            event.setLatitude(eventForm.getLatitude());
            event.setLongitude(eventForm.getLongitude());
        }
    }

    private void updateEventAttendance(EventFormDto eventForm, Event eventDB) {
        if (!eventForm.isActivateAttendanceList() || eventDB.getGroupId().isNotUnit()) {
            eventDB.setActiveAttendanceList(false);
            eventDB.setActiveAttendancePayment(false);
            eventDB.setClosedAttendanceList(false);
            confirmationService.deleteAllByEventId(eventDB.getId());
        } else if (!eventDB.isActiveAttendanceList()) {
            eventDB.setActiveAttendanceList(true);
            eventDB.setActiveAttendancePayment(eventForm.isActivateAttendancePayment());
            eventDB.setClosedAttendanceList(false);
            this.scoutService.findAllByLoggedScouterGroupId().forEach(scout -> {
                Confirmation confirmation = new Confirmation();
                confirmation.setEvent(eventDB);
                confirmation.setScout(scout);
                if (eventForm.isActivateAttendancePayment()) confirmation.setPayed(false);
                confirmationService.save(confirmation);
            });
        } else {
            eventDB.setClosedAttendanceList(eventForm.isCloseAttendanceList());
            if (!eventDB.isActiveAttendancePayment() && eventForm.isActivateAttendancePayment()) {
                eventDB.setActiveAttendancePayment(true);
                eventDB.getConfirmationList().forEach(confirmation -> {
                    confirmation.setPayed(false);
                    confirmationService.save(confirmation);
                });
            } else if (eventDB.isActiveAttendancePayment() && !eventForm.isActivateAttendancePayment()) {
                eventDB.setActiveAttendancePayment(false);
                eventDB.getConfirmationList().stream().filter(confirmation -> confirmation.getPayed() != null).forEach(confirmation -> {
                    confirmation.setPayed(null);
                    confirmationService.save(confirmation);
                });
            }
        }
    }

    @Override
    public void delete(Integer id) {
        eventRepository.deleteById(id);
    }
}
