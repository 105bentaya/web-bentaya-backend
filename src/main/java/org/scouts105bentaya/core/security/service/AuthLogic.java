package org.scouts105bentaya.core.security.service;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.entity.BookingDocument;
import org.scouts105bentaya.features.booking.entity.OwnBooking;
import org.scouts105bentaya.features.booking.enums.BookingDocumentStatus;
import org.scouts105bentaya.features.booking.enums.BookingStatus;
import org.scouts105bentaya.features.booking.repository.BookingDocumentRepository;
import org.scouts105bentaya.features.booking.repository.OwnBookingRepository;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.dto.EventFormDto;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.service.ScoutService;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthLogic {

    private final AuthService authService;
    private final ScoutService scoutService;
    private final EventService eventService;
    private final PreScoutService preScoutService;
    private final BookingDocumentRepository bookingDocumentRepository;
    private final OwnBookingRepository ownBookingRepository;

    public AuthLogic(
        AuthService authService,
        ScoutService scoutService,
        EventService eventService,
        PreScoutService preScoutService,
        BookingDocumentRepository bookingDocumentRepository,
        OwnBookingRepository ownBookingRepository
    ) {
        this.authService = authService;
        this.scoutService = scoutService;
        this.eventService = eventService;
        this.preScoutService = preScoutService;
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.ownBookingRepository = ownBookingRepository;
    }

    //todo maybe reuse
//    public boolean userHasSameGroupIdAsScout(int scoutId) {
//        Group loggedUserGroup = authService.getLoggedUser().getGroup();
//        if (loggedUserGroup == null) return false;
//        Group scoutGroup = scoutService.findActiveById(scoutId).getGroup();
//        return loggedUserGroup.getId().equals(scoutGroup.getId());
//    }

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

    public boolean scouterHasAccessToBooking(int bookingId) {
        OwnBooking booking = ownBookingRepository.get(bookingId);
        if (booking.getGroup() == null) return true;
        Group eventGroup = booking.getGroup();
        Group loggedUserGroup = authService.getLoggedUser().getGroup();
        return loggedUserGroup != null && Objects.equals(Objects.requireNonNull(eventGroup).getId(), loggedUserGroup.getId());
    }

    public boolean scouterHasGroupId(Integer groupId) {
        Group loggedUserGroup = authService.getLoggedUser().getGroup();
        if (loggedUserGroup == null) return false;
        return loggedUserGroup.getId().equals(groupId);
    }

    public boolean preScoutHasGroupId(int preScoutId, int groupId) {
        PreScoutAssignation preScoutAssignation = preScoutService.findById(preScoutId).getPreScoutAssignation();
        if (preScoutAssignation == null) return false;
        return groupId == Objects.requireNonNull(preScoutAssignation.getGroup()).getId();
    }

    public boolean scouterHasPreScoutGroupId(int preScoutId) {
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

    public boolean userHasAccessToScoutCenter(int scoutCenterId) {
        LocalDateTime maxIncidenceTime = LocalDateTime.now().minusDays(7);
        return authService.getLoggedUser().getBookingList().stream()
            .anyMatch(booking -> booking.getScoutCenter().getId().equals(scoutCenterId) &&
                                 booking.getStatus().reservedOrOccupied() &&
                                 booking.getEndDate().isAfter(maxIncidenceTime)
            );
    }

    public boolean userOwnsBookingDocumentFile(int documentId) {
        return userOwnsBookingDocumentFile(bookingDocumentRepository.get(documentId));
    }

    public boolean userCanEditBookingDocumentFile(int documentId) {
        BookingDocument bookingDocument = bookingDocumentRepository.get(documentId);
        return userOwnsBookingDocumentFile(bookingDocument) && bookingDocumentIsEditable(bookingDocument);
    }

    private boolean userOwnsBookingDocumentFile(BookingDocument bookingDocument) {
        return bookingDocument.getFile().getUser().getId().equals(authService.getLoggedUser().getId());
    }

    private static Boolean bookingDocumentIsEditable(BookingDocument bookingDocument) {
        return bookingDocument.getBooking().getStatus().equals(BookingStatus.RESERVED) &&
               !bookingDocument.getStatus().equals(BookingDocumentStatus.ACCEPTED);
    }
}
