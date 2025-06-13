package org.scouts105bentaya.core.security.service;

import jakarta.annotation.Nullable;
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
import org.scouts105bentaya.features.scout.dto.form.NewScoutFormDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.enums.ScoutFileType;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthLogic {

    private final AuthService authService;
    private final EventService eventService;
    private final PreScoutService preScoutService;
    private final BookingDocumentRepository bookingDocumentRepository;
    private final OwnBookingRepository ownBookingRepository;
    private final ScoutRepository scoutRepository;
    private final ScoutFileRepository scoutFileRepository;

    public AuthLogic(
        AuthService authService,
        EventService eventService,
        PreScoutService preScoutService,
        BookingDocumentRepository bookingDocumentRepository,
        OwnBookingRepository ownBookingRepository,
        ScoutRepository scoutRepository,
        ScoutFileRepository scoutFileRepository
    ) {
        this.authService = authService;
        this.eventService = eventService;
        this.preScoutService = preScoutService;
        this.bookingDocumentRepository = bookingDocumentRepository;
        this.ownBookingRepository = ownBookingRepository;
        this.scoutRepository = scoutRepository;
        this.scoutFileRepository = scoutFileRepository;
    }

    public boolean eventIsEditableByScouter(EventFormDto eventFormDto) {
        Event event;
        try {
            event = eventService.findById(eventFormDto.id());
        } catch (WebBentayaNotFoundException e) {
            return false;
        }
        Group loggedUserGroup = getLoggedUserScouterGroup();
        if (!event.isForEveryone() && (loggedUserGroup == null || !Objects.equals(loggedUserGroup.getId(), Objects.requireNonNull(event.getGroup()).getId()))) return false;
        return eventFormDto.forEveryone() || (loggedUserGroup != null && Objects.equals(loggedUserGroup.getId(), eventFormDto.groupId()));
    }

    public boolean scouterHasAccessToEvent(int eventId) {
        Event event = eventService.findById(eventId);
        if (event.isForEveryone()) return true;
        Group eventGroup = event.getGroup();
        Group loggedUserGroup = getLoggedUserScouterGroup();
        return loggedUserGroup != null && Objects.equals(Objects.requireNonNull(eventGroup).getId(), loggedUserGroup.getId());
    }

    public boolean scouterHasAccessToBooking(int bookingId) {
        OwnBooking booking = ownBookingRepository.get(bookingId);
        if (booking.getGroup() == null) return true;
        Group eventGroup = booking.getGroup();
        Group loggedUserGroup = getLoggedUserScouterGroup();
        return loggedUserGroup != null && Objects.equals(Objects.requireNonNull(eventGroup).getId(), loggedUserGroup.getId());
    }

    public boolean scouterHasGroupId(Integer groupId) {
        Group loggedUserGroup = getLoggedUserScouterGroup();
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
        Group loggedUserGroup = getLoggedUserScouterGroup();
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

    // SCOUT CONTROLS

    public boolean userHasAccessToScout(int scoutId) {
        User loggedUser = authService.getLoggedUser();
        return loggedUser.hasRole(RoleEnum.ROLE_USER) && loggedUser.getScoutList().stream().anyMatch(scout -> scout.getId().equals(scoutId));
    }

    public boolean isUserWithAccessToScoutFile(int fileId) {
        User user = authService.getLoggedUser();
        if (!user.hasRole(RoleEnum.ROLE_USER)) return false;

        ScoutFile file = scoutFileRepository.findById(fileId).orElse(null);
        if (file == null) return false;

        return user.getScoutList().stream().anyMatch(scout ->
            hasDocumentWithId(scout.getMedicalData().getDocuments(), fileId) ||
            hasDocumentWithId(scout.getEconomicData().getDocuments(), fileId) ||
            hasDocumentWithId(scout.getPersonalData().getDocuments(), fileId)
        );
    }

    private boolean hasDocumentWithId(List<ScoutFile> files, int fileId) {
        return files.stream().anyMatch(doc -> doc.getId().equals(fileId));
    }

    public boolean isScouterAndCanEditScout(int scoutId) {
        User user = authService.getLoggedUser();
        if (!user.hasRole(RoleEnum.ROLE_SCOUTER)) return false;
        return isScouterOwner(user, scoutId) || isScouterOfScout(user, scoutId);
    }

    public boolean isScouterAndCanEditGroupScout(int scoutId) {
        User user = authService.getLoggedUser();
        if (!user.hasRole(RoleEnum.ROLE_SCOUTER)) return false;
        return isScouterOfScout(user, scoutId);
    }

    private boolean isScouterOwner(User user, int scoutId) {
        return Optional.ofNullable(user.getScouter())
            .map(scouter -> scouter.getId().equals(scoutId))
            .orElse(false);
    }

    private boolean isScouterOfScout(User user, int scoutId) {
        Optional<Group> scoutGroup = scoutRepository.findScoutGroup(scoutId);
        Optional<Group> scouterGroup = Optional.ofNullable(user.getScouter()).map(Scout::getGroup);

        return scoutGroup.isPresent() && scouterGroup
            .map(group -> group.getId().equals(scoutGroup.get().getId()))
            .orElse(false);
    }

    private @Nullable Group getLoggedUserScouterGroup() {
        return getUserScouterGroup(this.authService.getLoggedUser());
    }

    private @Nullable Group getUserScouterGroup(User user) {
        return Optional.ofNullable(user.getScouter()).map(Scout::getGroup).orElse(null);
    }

    public boolean isScouterAndCanUploadDocument(int entityId, ScoutFileType fileType) {
        if (fileType == ScoutFileType.RECORD) return false;
        User user = authService.getLoggedUser();
        if (!user.hasRole(RoleEnum.ROLE_SCOUTER)) return false;

        return isScouterOwner(user, entityId) || isScouterOfScout(user, entityId);
    }

    public boolean isScouterAndCanAddScout(NewScoutFormDto newScoutFormDto) {
        User user = authService.getLoggedUser();
        if (!user.hasRole(RoleEnum.ROLE_SCOUTER)) return false;
        if (!newScoutFormDto.scoutType().isScoutOrScouter()) return false;
        return newScoutFormDto.census() == null;
    }
}
