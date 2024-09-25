package org.scouts105bentaya.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.scouts105bentaya.dto.event.EventFormDto;
import org.scouts105bentaya.entity.Confirmation;
import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.entity.Scout;
import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.enums.Group;
import org.scouts105bentaya.exception.EventNotFoundException;
import org.scouts105bentaya.exception.WebBentayaException;
import org.scouts105bentaya.repository.EventRepository;
import org.scouts105bentaya.service.AuthService;
import org.scouts105bentaya.service.ConfirmationService;
import org.scouts105bentaya.service.EventService;
import org.scouts105bentaya.service.ScoutService;
import org.scouts105bentaya.service.UserService;
import org.scouts105bentaya.util.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.beans.Transient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EventServiceImpl implements EventService {

    private static final ZoneId ZONE = ZoneId.of("UTC");

    private final EventRepository eventRepository;

    private final ScoutService scoutService;
    private final ConfirmationService confirmationService;
    private final AuthService authService;
    private final UserService userService;

    @Value("${jwt.calendar.key}")
    private String secret;

    public EventServiceImpl(
        EventRepository eventRepository,
        ScoutService scoutService,
        ConfirmationService confirmationService,
        AuthService authService,
        UserService userService
    ) {
        this.eventRepository = eventRepository;
        this.scoutService = scoutService;
        this.confirmationService = confirmationService;
        this.authService = authService;
        this.userService = userService;
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
            event.setStartDate(ZonedDateTime.of(eventForm.getLocalStartDate(), LocalTime.of(11, 0), ZONE));
            event.setEndDate(ZonedDateTime.of(eventForm.getLocalEndDate(), LocalTime.of(13, 0), ZONE));
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

    @Override
    public byte[] getIcsCalendar(String jwtToken) {
        Jws<Claims> parsedToken = JwtUtils.decodeJwtToken(jwtToken, secret);

        if (parsedToken == null) throw new WebBentayaException("No se ha podido generar el calendario");
        if (parsedToken.getBody().getIssuedAt() == null) throw new WebBentayaException("Este link es inválido");

        User user = userService.findById(parsedToken.getBody().get("usr", Integer.class));
        if (!user.isEnabled()) throw new WebBentayaException("Usuario no autorizado");
        Set<Group> groups = getUserGroupIds(user);
        if (groups.isEmpty()) throw new WebBentayaException("Usuario no autorizado");

        Calendar calendar = new Calendar();
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);
        calendar.add(new ProdId("-//Scouts 105 Bentaya//Calendario Web Bentaya//ES-es"));
        calendar.add(new Name("Calendario Scouts 105 Bentaya"));

        List<Event> events = groups.stream().map(this::findAllByGroupId).flatMap(Collection::stream).toList();
        events.forEach(event -> calendar.add(this.generateICSEvent(event)));

        try {
            ByteArrayOutputStream output =  new ByteArrayOutputStream();
            CalendarOutputter outputWriter = new CalendarOutputter();
            outputWriter.setValidating(false);
            outputWriter.output(calendar, output);
            return output.toByteArray();
        } catch (IOException ioException) {
            throw new WebBentayaException("No se ha podido generar el calendario");
        }
    }

    private VEvent generateICSEvent(Event event) {
        VEvent calEvent = new VEvent();
        calEvent.add(new Uid("event105bentaya-%s".formatted(event.getId())));
        calEvent.add(new Summary(event.getTitle()));
        calEvent.add(new Location(event.getLocation()));
        calEvent.add(new Description(generateEventDescription(event)));

        if (event.isUnknownTime()) {
            calEvent.add(new DtStart<>(event.getStartDate().withZoneSameInstant(ZONE).toLocalDate()));
            calEvent.add(new DtEnd<>(event.getEndDate().withZoneSameInstant(ZONE).toLocalDate().plusDays(1)));
        } else {
            calEvent.add(new DtStart<>(event.getStartDate()));
            calEvent.add(new DtEnd<>(event.getEndDate()));
        }
        return calEvent;
    }

    //todo añadir consultas a asistencia, etc, (habrá que crear lo del link de asistencia)
    private String generateEventDescription(Event event) {
        return """
            Evento de %s
            %s
            """.formatted(event.getGroupId().toTitleCase(), event.getDescription());
    }

    @Override
    public String getCalendarSubscription() {
        User user = authService.getLoggedUser();

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        //todo add issue time to unable all tokens that are 'deactivated' by the user
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("usr", user.getId())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            //.claim("grp", getUserGroupIds(user)) //todo add form dto and ask users which groups to subscribe
            .signWith(key)
            .compact();
    }

    private Set<Group> getUserGroupIds(User user) {
        Set<Group> groups = new HashSet<>();
        groups.add(Group.GRUPO);
        if (user.hasRole("ROLE_USER")) groups.addAll(user.getScoutList().stream().map(Scout::getGroupId).toList());
        if (user.hasRole("ROLE_SCOUTER")) groups.addAll(List.of(user.getGroupId(), Group.SCOUTERS));
        else if (user.hasRole("ROLE_GROUP_SCOUTER")) groups.add(Group.SCOUTERS);
        return groups;
    }
}
