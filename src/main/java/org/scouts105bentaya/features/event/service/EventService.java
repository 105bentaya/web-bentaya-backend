package org.scouts105bentaya.features.event.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.EventRepository;
import org.scouts105bentaya.features.event.dto.EventDateConflictsFormDto;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.features.group.GroupService;
import org.scouts105bentaya.features.scout.service.ScoutService;
import org.scouts105bentaya.shared.GenericConstants;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;

    private final ScoutService scoutService;
    private final ConfirmationService confirmationService;
    private final GroupService groupService;

    public EventService(
        EventRepository eventRepository,
        ScoutService scoutService,
        ConfirmationService confirmationService,
        GroupService groupService
    ) {
        this.eventRepository = eventRepository;
        this.scoutService = scoutService;
        this.confirmationService = confirmationService;
        this.groupService = groupService;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(Integer id) {
        return eventRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    @Transactional
    public Event save(EventFormDto eventForm) {
        Event newEvent = new Event();

        this.setEventBasicInfo(eventForm, newEvent);

        if ((newEvent.getGroup() == null || newEvent.isForScouters()) && eventForm.activateAttendanceList()) {
            throw new WebBentayaBadRequestException("No se puede activar la asistencia para esta unidad");
        }

        newEvent.setActiveAttendanceList(eventForm.activateAttendanceList());
        newEvent.setActiveAttendancePayment(eventForm.activateAttendanceList() && eventForm.activateAttendancePayment());
        newEvent.setClosedAttendanceList(eventForm.activateAttendanceList() && eventForm.closeAttendanceList());
        newEvent.setCloseDateTime(!eventForm.activateAttendanceList() || eventForm.closeAttendanceList() ? null : eventForm.closeDateTime());

        this.setEventDates(newEvent, eventForm);

        Event savedEvent = eventRepository.save(newEvent);
        if (savedEvent.isActiveAttendanceList()) {
            this.scoutService.findAllScoutsByLoggedScouterGroupId().forEach(scout -> {
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

        this.setEventBasicInfo(eventForm, eventDB);
        this.setEventDates(eventDB, eventForm);
        this.updateEventAttendance(eventForm, eventDB);

        return eventRepository.save(eventDB);
    }

    private void setEventBasicInfo(EventFormDto eventForm, Event event) {
        event.setTitle(eventForm.title());
        event.setForEveryone(eventForm.forEveryone());
        if (!event.isForEveryone()) {
            event.setGroup(groupService.findById(Objects.requireNonNull(eventForm.groupId())));
        } else {
            event.setGroup(null);
        }
        event.setForScouters(eventForm.forScouters());
        event.setDescription(eventForm.description());
        event.setLocation(eventForm.location());
        event.setMeetingLocation(eventForm.meetingLocation());
        event.setPickupLocation(eventForm.pickupLocation());
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

    private void updateEventAttendance(EventFormDto eventForm, Event eventDB) {
        if (!eventForm.activateAttendanceList() || eventDB.getGroup() == null || eventDB.isForScouters()) {
            eventDB.setActiveAttendanceList(false);
            eventDB.setActiveAttendancePayment(false);
            eventDB.setClosedAttendanceList(false);
            eventDB.setCloseDateTime(null);
            confirmationService.deleteAllByEventId(eventDB.getId());
        } else if (!eventDB.isActiveAttendanceList()) {
            eventDB.setActiveAttendanceList(true);
            eventDB.setActiveAttendancePayment(eventForm.activateAttendancePayment());
            eventDB.setClosedAttendanceList(eventForm.closeAttendanceList());
            eventDB.setCloseDateTime(eventForm.closeAttendanceList() ? null : eventForm.closeDateTime());
            this.scoutService.findAllScoutsByLoggedScouterGroupId().forEach(scout -> {
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

    public List<GroupBasicDataDto> getEventDateConflicts(EventDateConflictsFormDto form) {
        List<Event> coincidingEvents = eventRepository.findEventsWithStartOrEndCoincidence(form.startDate(), form.endDate());

        Stream<Event> coincidingEventsStream;
        if (form.groupId() != null) {
            coincidingEventsStream = coincidingEvents.stream()
                .filter(Predicate.not(
                    event -> Optional.ofNullable(event.getGroup())
                        .map(group -> group.getId().equals(form.groupId()))
                        .orElse(false)
                ));
        } else {
            coincidingEventsStream = coincidingEvents.stream().filter(event -> !event.isForEveryone());
        }

        return coincidingEventsStream
            .map(Event::getGroup)
            .distinct()
            .map(GroupBasicDataDto::fromGroupNullAsGeneral)
            .toList();
    }
}
