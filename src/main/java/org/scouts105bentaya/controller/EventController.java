package org.scouts105bentaya.controller;

import org.scouts105bentaya.converter.event.EventCalendarConverter;
import org.scouts105bentaya.converter.event.EventConverter;
import org.scouts105bentaya.converter.event.EventFormConverter;
import org.scouts105bentaya.dto.event.EventCalendarDto;
import org.scouts105bentaya.dto.event.EventDto;
import org.scouts105bentaya.dto.event.EventFormDto;
import org.scouts105bentaya.exception.WebBentayaException;
import org.scouts105bentaya.service.CalendarService;
import org.scouts105bentaya.service.EventService;
import org.scouts105bentaya.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/event")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
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

    @PostFilter("hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'ADMIN') ? true : @authLogic.groupIdIsUserAuthorized(filterObject.groupId)")
    @GetMapping
    public List<EventCalendarDto> findAll() {
        return eventService.findAll().stream()
            .map(eventCalendarConverter::convertFromEntity).
            toList();
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'USER')")
    @GetMapping("/subscribe")
    public String subscribeToCalendar() {
        log.info("METHOD EventController.subscribeToCalendar{}", SecurityUtils.getLoggedUserUsernameForLog());
        return (calendarService.getCalendarSubscription());
    }

    @GetMapping("/public/calendar")
    public ResponseEntity<byte[]> getCalendar(@RequestParam String token) {
        log.info("METHOD EventController.getCalendar");
        return ResponseEntity.ok()
            .header("Content-Type", "text/calendar")
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bentaya-calendar.ics")
            .body(calendarService.getIcsCalendar(token));
    }

    @PostAuthorize("@authLogic.groupIdIsUserAuthorized(returnObject.groupId) or hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'ADMIN')")
    @GetMapping("/get/{id}")
    public EventDto findById(@PathVariable Integer id) {
        log.info("METHOD EventController.findById --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return eventConverter.convertFromEntity(eventService.findById(id));
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'ADMIN') and @authLogic.eventIsEditableByUser(#id)")
    @GetMapping("/edit/{id}")
    public EventFormDto findByIdToEdit(@PathVariable Integer id) {
        log.info("METHOD EventController.findByIdToEdit --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return eventFormConverter.convertFromEntity(eventService.findById(id));
    }

    @PreAuthorize("(hasRole('SCOUTER') and (@authLogic.groupIdIsNotUnit(#event.groupId) or @authLogic.userHasGroupId(#event.groupId))) or (hasAnyRole('GROUP_SCOUTER', 'ADMIN') and @authLogic.groupIdIsNotUnit(#event.groupId))")
    @PostMapping
    public EventDto save(@RequestBody EventFormDto event) {
        log.info("METHOD EventController.save{}", SecurityUtils.getLoggedUserUsernameForLog());
        return eventConverter.convertFromEntity(eventService.save(event));
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'ADMIN') and @authLogic.eventIsEditableByUser(#eventDto.id) and (@authLogic.groupIdIsNotUnit(#eventDto.groupId) or @authLogic.userHasGroupId(#eventDto.groupId))")
    @PutMapping
    public EventDto update(@RequestBody EventFormDto eventDto) {
        log.info("METHOD EventController.update --- PARAMS id: {}{}", eventDto.id(), SecurityUtils.getLoggedUserUsernameForLog());
        return eventConverter.convertFromEntity(eventService.update(eventDto));
    }

    @PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER', 'ADMIN') and @authLogic.eventIsEditableByUser(#id)")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("METHOD EventController.delete --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        eventService.delete(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebBentayaException.class)
    public Map<String, String> handlePdfException(WebBentayaException e) {
        return Map.of("message", e.getMessage());
    }
}
