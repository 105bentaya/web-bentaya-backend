package org.scouts105bentaya.core.security.service;

import org.scouts105bentaya.features.booking.entity.Booking;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.pre_scout.PreScoutService;
import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;
import org.scouts105bentaya.features.scout.ScoutService;
import org.scouts105bentaya.shared.Group;
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
        Group loggedUserGroup = authService.getLoggedUser().getGroupId();
        if (loggedUserGroup == null) return false;
        Group scoutGroup = scoutService.findById(scoutId).getGroupId();
        return loggedUserGroup.equals(scoutGroup);
    }

    public boolean eventIsEditableByUser(int eventId) {
        Group eventGroup = eventService.findById(eventId).getGroupId();
        if (eventGroup.isNotUnit()) return true;
        Group loggedUserGroup = authService.getLoggedUser().getGroupId();
        return Objects.equals(eventGroup, loggedUserGroup);
    }

    public boolean userHasGroupId(int groupId) {
        return Objects.equals(authService.getLoggedUser().getGroupId(), Group.valueOf(groupId));
    }

    public boolean preScoutHasGroupId(int preScoutId, int groupId) {
        PreScoutAssignation preScoutAssignation = preScoutService.findById(preScoutId).getPreScoutAssignation();
        if (preScoutAssignation == null) return false;
        return Objects.equals(Group.valueOf(groupId), preScoutAssignation.getGroupId());
    }

    public boolean userHasPreScoutId(int preScoutId) {
        PreScoutAssignation preScoutAssignation = preScoutService.findById(preScoutId).getPreScoutAssignation();
        if (preScoutAssignation == null) return false;
        return Objects.equals(authService.getLoggedUser().getGroupId(), preScoutAssignation.getGroupId());
    }

    public boolean userHasScoutId(int scoutId) {
        return this.authService.getLoggedUser().getScoutList().stream().anyMatch(scout -> scout.getId().equals(scoutId));
    }

    public boolean groupIdIsNotUnit(int groupId) {
        Group group = Group.valueOf(groupId);
        return group != null && group.isNotUnit();
    }

    public boolean groupIdIsUserAuthorized(int groupId) {
        Group group = Group.valueOf(groupId);
        return group != null && group.isUserAuthorized();
    }

    public boolean userOwnsBooking(int bookingId) {
        return authService.getLoggedUser().getBookingList().stream()
                .anyMatch(booking -> booking.getId().equals(bookingId));
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
