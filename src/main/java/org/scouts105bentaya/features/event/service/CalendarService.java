package org.scouts105bentaya.features.event.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.core.exception.WebBentayaForbiddenException;
import org.scouts105bentaya.core.exception.WebBentayaUnauthorizedException;
import org.scouts105bentaya.core.security.InvalidJwtException;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.util.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CalendarService {
    private static final String CALENDAR_GENERATION_ERROR = "No se ha podido generar el calendario";
    private static final String TOKEN_USER_GROUPS_KEY = "userGroups";

    private final UserService userService;
    private final EventService eventService;
    private final AuthService authService;
    @Value("${jwt.calendar.key}") private String secret;
    @Value("${bentaya.web.url}") private String url;

    public CalendarService(
        UserService userService,
        EventService eventService,
        AuthService authService
    ) {
        this.userService = userService;
        this.eventService = eventService;
        this.authService = authService;
    }

    public byte[] getIcsCalendar(String jwtToken) {
        try {
            return this.getIcsCalendarAsByteArray(jwtToken);
        } catch (InvalidJwtException e) {
            throw new WebBentayaBadRequestException(CALENDAR_GENERATION_ERROR);
        } catch (IOException e) {
            throw new WebBentayaErrorException(CALENDAR_GENERATION_ERROR);
        }
    }

    private byte[] getIcsCalendarAsByteArray(String jwtToken) throws InvalidJwtException, IOException {
        Jws<Claims> parsedToken = JwtUtils.decodeJwtToken(jwtToken, secret);
        if (parsedToken.getPayload().getIssuedAt() == null) {
            log.warn("getIcsCalendarAsByteArray - JWT token has no 'issued at' field");
            throw new WebBentayaBadRequestException("Este link es inválido");
        }

        User user = userService.findById(parsedToken.getPayload().get("usr", Integer.class));
        if (!user.isEnabled()) {
            log.warn("getIcsCalendarAsByteArray - user is not enabled");
            throw new WebBentayaForbiddenException("Usuario deshabilitado");
        }

        log.info("getIcsCalendarAsByteArray - generating calendar for {}", user.getUsername());
        Calendar calendar = new Calendar();
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);
        calendar.add(new ProdId("-//Scouts 105 Bentaya//Calendario Web Bentaya//ES-es"));
        calendar.add(new Name("Calendario Scouts 105 Bentaya"));

        TokenGroups tokenGroups = getTokenGroups(user, parsedToken.getPayload());
        List<Event> events = getEvents(tokenGroups);

        events.forEach(event -> calendar.add(this.generateICSEvent(event)));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CalendarOutputter outputWriter = new CalendarOutputter();
        outputWriter.setValidating(false);
        outputWriter.output(calendar, output);
        return output.toByteArray();
    }

    private VEvent generateICSEvent(Event event) {
        VEvent calEvent = new VEvent();
        calEvent.add(new Uid("event105bentaya-%s".formatted(event.getId())));
        calEvent.add(new Summary(event.getTitle()));
        calEvent.add(new Location(event.getLocation()));
        calEvent.add(new Description(generateEventDescription(event)));
        try {
            calEvent.add(new Url(new URI("%s/calendario?actividad=%s".formatted(url, event.getId()))));
        } catch (URISyntaxException e) {
            throw new WebBentayaErrorException(CALENDAR_GENERATION_ERROR);
        }

        if (event.isUnknownTime()) {
            calEvent.add(new DtStart<>(event.getStartDate().withZoneSameInstant(GenericConstants.UTC_ZONE).toLocalDate()));
            calEvent.add(new DtEnd<>(event.getEndDate().withZoneSameInstant(GenericConstants.UTC_ZONE).toLocalDate().plusDays(1)));
        } else {
            calEvent.add(new DtStart<>(event.getStartDate()));
            calEvent.add(new DtEnd<>(event.getEndDate()));
        }
        return calEvent;
    }

    private String generateEventDescription(Event event) {
        String eventGroupTitle = event.isForEveryone() ? "grupo" : Objects.requireNonNull(event.getGroup()).getName();
        if (event.isForScouters()) eventGroupTitle = "%s (sólo scouters)".formatted(eventGroupTitle);

        String description = "Actividad de %s.".formatted(eventGroupTitle);

        if (!StringUtils.isBlank(event.getDescription())) {
            description += "\n" + event.getDescription();
            if (!description.endsWith(".")) description += ".";
        }

        if (event.isUnknownTime()) description += "\nEl horario está aún por concretar.";
        if (event.isActiveAttendanceList()) {
            description += "\nPara acceder a la asistencia entre al link del evento";
        }
        return description;
    }

    public String getCalendarSubscription() {
        User user = authService.getLoggedUser();

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
            .header()
            .type("JWT")
            .and()
            .claim("usr", user.getId())
            .claim(TOKEN_USER_GROUPS_KEY, true)
            .issuedAt(new Date(System.currentTimeMillis()))
            .signWith(key)
            .compact();
    }

    private TokenGroups getTokenGroups(User user, Claims claims) {
        if (!user.isMember()) {
            throw new WebBentayaUnauthorizedException("Usuario no autorizado a acceder al calendario");
        }

        TokenGroups tokenGroups = new TokenGroups();
        Set<Group> groups = new HashSet<>();

        if (claims.containsKey(TOKEN_USER_GROUPS_KEY) && Boolean.TRUE.equals(claims.get(TOKEN_USER_GROUPS_KEY, Boolean.class))) {
            tokenGroups.setGroupEvents(true);
            if (user.hasRole(RoleEnum.ROLE_USER)) {
                groups.addAll(user.getScoutList().stream().map(Scout::getGroup).toList());
            }
            if (user.hasRole(RoleEnum.ROLE_SCOUTER)) {
                tokenGroups.setScouterEvents(true);
                groups.add(Objects.requireNonNull(user.getGroup()));
            } else if (user.hasRole(RoleEnum.ROLE_GROUP_SCOUTER)) {
                tokenGroups.setScouterEvents(true);
            }
        }
        tokenGroups.setGroups(groups);
        return tokenGroups;
    }

    private List<Event> getEvents(TokenGroups tokenGroups) {
        return eventService.findAll().stream()
            .filter(event -> tokenGroups.groupEvents || !event.isForEveryone())
            .filter(event -> tokenGroups.scouterEvents || !event.isForScouters())
            .filter(event -> event.isForEveryone() || tokenGroups.groups.contains(event.getGroup()))
            .collect(Collectors.toList());
    }

    @Getter
    @Setter
    private static class TokenGroups {
        private boolean groupEvents;
        private boolean scouterEvents;
        private Set<Group> groups;
    }
}
