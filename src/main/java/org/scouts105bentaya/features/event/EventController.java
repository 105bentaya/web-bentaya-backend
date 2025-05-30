package org.scouts105bentaya.features.event;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.event.converter.EventCalendarConverter;
import org.scouts105bentaya.features.event.converter.EventConverter;
import org.scouts105bentaya.features.event.converter.EventFormConverter;
import org.scouts105bentaya.features.event.dto.CalendarDto;
import org.scouts105bentaya.features.event.dto.EventCalendarDto;
import org.scouts105bentaya.features.event.dto.EventDateConflictsFormDto;
import org.scouts105bentaya.features.event.dto.EventDto;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.event.service.CalendarService;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.group.GroupBasicDataDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/event")
public class EventController {

    private final EventService eventService;
    private final EventCalendarConverter eventCalendarConverter;
    private final EventFormConverter eventFormConverter;
    private final CalendarService calendarService;
    private final EventConverter eventConverter;

    public EventController(
        EventService eventService,
        EventCalendarConverter eventCalendarConverter,
        EventFormConverter eventFormConverter,
        CalendarService calendarService,
        EventConverter eventConverter
    ) {
        this.eventService = eventService;
        this.eventCalendarConverter = eventCalendarConverter;
        this.eventFormConverter = eventFormConverter;
        this.calendarService = calendarService;
        this.eventConverter = eventConverter;
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'USER')")
    @PostFilter("hasAnyRole('SCOUTER', 'GROUP_SCOUTER') ? true : !filterObject.forScouters")
    @GetMapping
    public List<EventCalendarDto> findAll() {
        return eventCalendarConverter.convertEntityCollectionToDtoList(eventService.findAll());
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'USER')")
    @GetMapping("/subscribe")
    public String subscribeToCalendar() {
        log.info("METHOD EventController.subscribeToCalendar{}", SecurityUtils.getLoggedUserUsernameForLog());
        return (calendarService.getCalendarSubscription());
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping("/coincidences")
    public List<GroupBasicDataDto> getEventDateConflicts(@Valid EventDateConflictsFormDto formDto) {
        log.info("METHOD EventController.getEventDateConflicts{}", SecurityUtils.getLoggedUserUsernameForLog());
        return eventService.getEventDateConflicts(formDto);
    }

    @GetMapping("/public/calendar")
    public ResponseEntity<byte[]> getCalendar(@RequestParam String token) {
        log.info("METHOD EventController.getCalendar");
        CalendarDto calendarDto = calendarService.getIcsCalendar(token);
        return ResponseEntity
            .status(calendarDto.status())
            .header("Content-Type", "text/calendar")
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bentaya-calendar.ics")
            .body(calendarDto.calendar());
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'USER')")
    @PostAuthorize("!returnObject.forScouters or hasAnyRole('SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping("/get/{id}")
    public EventDto findById(@PathVariable Integer id) {
        log.info("METHOD EventController.findById --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return eventConverter.convertFromEntity(eventService.findById(id));
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER') and @authLogic.scouterHasAccessToEvent(#id)")
    @GetMapping("/edit/{id}")
    public EventFormDto findByIdToEdit(@PathVariable Integer id) {
        log.info("METHOD EventController.findByIdToEdit --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return eventFormConverter.convertFromEntity(eventService.findById(id));
    }

    @PreAuthorize("(hasRole('SCOUTER') and (#event.forEveryone or @authLogic.scouterHasGroupId(#event.groupId))) or (hasRole('GROUP_SCOUTER') and #event.forEveryone)")
    @PostMapping
    public EventDto save(@RequestBody @Valid EventFormDto event) {
        log.info("METHOD EventController.save{}", SecurityUtils.getLoggedUserUsernameForLog());
        return eventConverter.convertFromEntity(eventService.save(event));
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER') and @authLogic.eventIsEditableByScouter(#eventDto)")
    @PutMapping
    public EventDto update(@RequestBody @Valid EventFormDto eventDto) {
        log.info("METHOD EventController.update --- PARAMS id: {}{}", eventDto.id(), SecurityUtils.getLoggedUserUsernameForLog());
        return eventConverter.convertFromEntity(eventService.update(eventDto));
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER') and @authLogic.scouterHasAccessToEvent(#id)")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("METHOD EventController.delete --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        eventService.delete(id);
    }
}
