package org.scouts105bentaya.service;

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
import org.scouts105bentaya.constant.GenericConstants;
import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.entity.Scout;
import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.enums.Group;
import org.scouts105bentaya.enums.Roles;
import org.scouts105bentaya.exception.WebBentayaException;
import org.scouts105bentaya.util.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CalendarService {

    private static final String TOKEN_USER_GROUPS_KEY = "userGroups";

    @Value("${jwt.calendar.key}")
    private String secret;

    private final UserService userService;
    private final EventService eventService;
    private final AuthService authService;

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
        Jws<Claims> parsedToken = JwtUtils.decodeJwtToken(jwtToken, secret);

        if (parsedToken == null) throw new WebBentayaException("No se ha podido generar el calendario");
        if (parsedToken.getBody().getIssuedAt() == null) throw new WebBentayaException("Este link es inválido");

        User user = userService.findById(parsedToken.getBody().get("usr", Integer.class));
        if (!user.isEnabled()) throw new WebBentayaException("Usuario no autorizado");


        Calendar calendar = new Calendar();
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);
        calendar.add(new ProdId("-//Scouts 105 Bentaya//Calendario Web Bentaya//ES-es"));
        calendar.add(new Name("Calendario Scouts 105 Bentaya"));

        Set<Group> tokenGroups = getTokenGroups(user, parsedToken.getBody());
        List<Event> events = tokenGroups.stream().map(eventService::findAllByGroupId).flatMap(Collection::stream).toList();
        events.forEach(event -> calendar.add(this.generateICSEvent(event)));

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
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
            calEvent.add(new DtStart<>(event.getStartDate().withZoneSameInstant(GenericConstants.UTC_ZONE).toLocalDate()));
            calEvent.add(new DtEnd<>(event.getEndDate().withZoneSameInstant(GenericConstants.UTC_ZONE).toLocalDate().plusDays(1)));
        } else {
            calEvent.add(new DtStart<>(event.getStartDate()));
            calEvent.add(new DtEnd<>(event.getEndDate()));
        }
        return calEvent;
    }

    private String generateEventDescription(Event event) {
        String description = """
            Actividad de %s.
            %s""".formatted(event.getGroupId().toTitleCase(), event.getDescription());
        if (!description.endsWith(".")) description += ".";
        if (event.isUnknownTime()) description += "\nEl horario está aún por concretar.";
        if (event.isActiveAttendanceList())
            description += "\nPara acceder a la asistencia entre a https://105bentaya.org";
        return description;
    }

    public String getCalendarSubscription() {
        User user = authService.getLoggedUser();

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("usr", user.getId())
            .claim(TOKEN_USER_GROUPS_KEY, true)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(key)
            .compact();
    }

    private Set<Group> getTokenGroups(User user, Claims claims) {
        if (!user.isMember()) throw new WebBentayaException("Usuario no autorizado");
        Set<Group> groups = new HashSet<>();

        if (claims.containsKey(TOKEN_USER_GROUPS_KEY) && Boolean.TRUE.equals(claims.get(TOKEN_USER_GROUPS_KEY, Boolean.class))) {
            groups.add(Group.GRUPO);
            if (user.hasRole(Roles.ROLE_USER))
                groups.addAll(user.getScoutList().stream().map(Scout::getGroupId).toList());
            if (user.hasRole(Roles.ROLE_SCOUTER)) groups.addAll(List.of(user.getGroupId(), Group.SCOUTERS));
            else if (user.hasRole(Roles.ROLE_GROUP_SCOUTER)) groups.add(Group.SCOUTERS);
        }

        return groups;
    }
}
