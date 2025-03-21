package org.scouts105bentaya.core.security.service;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.ScoutService;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static org.scouts105bentaya.features.booking.enums.BookingStatus.RESERVED;

@Service
public class AuthLogic {

    private final AuthService authService;
    private final ScoutService scoutService;
    private final EventService eventService;
    private final PreScoutService preScoutService;

    public AuthLogic(
        AuthService authService,
        ScoutService scoutService,
        EventService eventService,
        PreScoutService preScoutService
    ) {
        this.authService = authService;
        this.scoutService = scoutService;
        this.eventService = eventService;
        this.preScoutService = preScoutService;
    }

    public boolean userHasSameGroupIdAsScout(int scoutId) {
        Group loggedUserGroup = authService.getLoggedUser().getGroup();
        if (loggedUserGroup == null) return false;
        Group scoutGroup = scoutService.findById(scoutId).getGroup();
        return loggedUserGroup.getId().equals(scoutGroup.getId());
    }

    public boolean eventIsEditableByScouter(EventFormDto eventFormDto) {
        Event event;
        try {
            event = eventService.findById(eventFormDto.id());
        } catch (WebBentayaNotFoundException e) {
            return false;
        }
        Group loggedUserGroup = authService.getLoggedUser().getGroup();
        if (!event.isForEveryone() && (loggedUserGroup == null || !Objects.equals(loggedUserGroup.getId(), Objects.requireNonNull(event.getGroup()).getId()))) return false;
        return eventFormDto.forEveryone() || (loggedUserGroup != null && Objects.equals(loggedUserGroup.getId(), eventFormDto.groupId()));
    }

    public boolean scouterHasAccessToEvent(int eventId) {
        Event event = eventService.findById(eventId);
        if (event.isForEveryone()) return true;
        Group eventGroup = event.getGroup();
        Group loggedUserGroup = authService.getLoggedUser().getGroup();
        return loggedUserGroup != null && Objects.equals(Objects.requireNonNull(eventGroup).getId(), loggedUserGroup.getId());
    }

    public boolean userHasGroupId(int groupId) {
        Group loggedUserGroup = authService.getLoggedUser().getGroup();
        if (loggedUserGroup == null) return false;
        return loggedUserGroup.getId() == groupId;
    }

    public boolean preScoutHasGroupId(int preScoutId, int groupId) {
        PreScoutAssignation preScoutAssignation = preScoutService.findById(preScoutId).getPreScoutAssignation();
        if (preScoutAssignation == null) return false;
        return groupId == Objects.requireNonNull(preScoutAssignation.getGroup()).getId();
    }

    public boolean userHasPreScoutGroupId(int preScoutId) {
        PreScoutAssignation preScoutAssignation = preScoutService.findById(preScoutId).getPreScoutAssignation();
        Group loggedUserGroup = authService.getLoggedUser().getGroup();
        if (preScoutAssignation == null || loggedUserGroup == null) return false;
        return Objects.equals(loggedUserGroup.getId(), Objects.requireNonNull(preScoutAssignation.getGroup()).getId());
    }

    public boolean userHasScoutId(int scoutId) {
        return this.authService.getLoggedUser().getScoutList().stream().anyMatch(scout -> scout.getId().equals(scoutId));
    }

    public boolean userOwnsBooking(int bookingId) {
        return authService.getLoggedUser().getBookingList().stream()
            .anyMatch(booking -> booking.getId().equals(bookingId));
    }

    public boolean userHasAccessToScoutCenter(int scoutCenterId) { //todo change so he only can access bookings that have not finalized, TFG-17
        return authService.getLoggedUser().getBookingList().stream()
            .anyMatch(booking -> booking.getScoutCenter().getId().equals(scoutCenterId));
    }

    public boolean userOwnsBookingDocument(int documentId) {
        return authService.getLoggedUser().getBookingList().stream()
            .map(Booking::getBookingDocumentList)
            .flatMap(Collection::stream)
            .anyMatch(bookingDocument -> bookingDocument.getId().equals(documentId));
    }

    public boolean userCanEditBookingDocument(int documentId) {
        Optional<BookingDocument> userBookingDocument = authService.getLoggedUser().getBookingList().stream()
            .map(Booking::getBookingDocumentList)
            .flatMap(Collection::stream)
            .filter(bookingDocument -> bookingDocument.getId().equals(documentId))
            .findFirst();

        if (userBookingDocument.isPresent()) {
            Booking booking = userBookingDocument.get().getBooking();
            return booking.getStatus().equals(RESERVED) && !booking.isUserConfirmedDocuments();
        }
        return false;
    }
}
