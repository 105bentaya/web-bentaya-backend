package org.scouts105bentaya.features.confirmation.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaForbiddenException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.ConfirmationId;
import org.scouts105bentaya.features.confirmation.ConfirmationRepository;
import org.scouts105bentaya.features.confirmation.dto.AttendanceListBasicDto;
import org.scouts105bentaya.features.confirmation.dto.AttendanceListUserDto;
import org.scouts105bentaya.features.confirmation.dto.AttendanceScoutEventInfo;
import org.scouts105bentaya.features.confirmation.dto.AttendanceScoutInfoDto;
import org.scouts105bentaya.features.confirmation.dto.ConfirmationDto;
import org.scouts105bentaya.features.event.Event;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.group.Group;
import org.scouts105bentaya.features.scout.Scout;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class ConfirmationService {

    private final ConfirmationRepository confirmationRepository;
    private final EventService eventService;
    private final AuthService authService;

    public ConfirmationService(
        ConfirmationRepository confirmationRepository,
        AuthService authService,
        @Lazy EventService eventService
    ) {
        this.confirmationRepository = confirmationRepository;
        this.eventService = eventService;
        this.authService = authService;
    }

    public Confirmation findById(Integer scoutId, Integer eventId) {
        return confirmationRepository
            .findById(new ConfirmationId(scoutId, eventId))
            .orElseThrow(WebBentayaNotFoundException::new);
    }

    public List<Confirmation> findAllByEventId(Integer id) {
        return confirmationRepository.findAllByEventId(id);
    }

    public List<AttendanceScoutEventInfo> findByLoggedUserScoutsAndEventId(Integer id) {
        List<Integer> loggedUserScoutIds = this.authService.getLoggedUser().getScoutList().stream()
            .map(Scout::getId)
            .toList();
        List<Confirmation> confirmations = confirmationRepository.findAllByEventIdInScoutIds(id, loggedUserScoutIds);
        return confirmations.stream().map(this::confirmationToAttendanceScoutEventInfo).toList();
    }

    private AttendanceScoutEventInfo confirmationToAttendanceScoutEventInfo(Confirmation confirmation) {
        return new AttendanceScoutEventInfo(
            confirmation.getAttending(),
            confirmation.getScout().getName(),
            confirmation.getScout().getId(),
            confirmation.getPayed()
        );
    }

    public List<AttendanceListBasicDto> findScouterAttendanceList() {
        return eventService.findAllByGroupIdAndActivatedAttendance(Objects.requireNonNull(authService.getLoggedUser().getGroup())).stream()
            .map(this::getAttendanceListBasicDtoFromEvent)
            .toList();
    }

    private AttendanceListBasicDto getAttendanceListBasicDtoFromEvent(Event event) {
        AttendanceListBasicDto attendanceListBasicDto = new AttendanceListBasicDto();
        attendanceListBasicDto.setEventTitle(event.getTitle());
        attendanceListBasicDto.setEventStartDate(event.getStartDate());
        attendanceListBasicDto.setEventEndDate(event.getEndDate());
        attendanceListBasicDto.setEventId(event.getId());
        attendanceListBasicDto.setEventHasPayment(event.isActiveAttendancePayment());
        attendanceListBasicDto.setEventIsClosed(event.eventAttendanceIsClosed());
        if (event.isActiveAttendancePayment()) attendanceListBasicDto.setAffirmativeAndPayedConfirmations(0);

        event.getConfirmationList().forEach(confirmation -> {
            if (Boolean.TRUE.equals(confirmation.getAttending())) {
                attendanceListBasicDto.incrementAffirmativeConfirmations();
                if (event.isActiveAttendancePayment() && Boolean.TRUE.equals(confirmation.getPayed())) {
                    attendanceListBasicDto.incrementAffirmativeAndPayedConfirmations();
                }
            } else if (Boolean.FALSE.equals(confirmation.getAttending())) {
                attendanceListBasicDto.incrementNegativeConfirmations();
            } else {
                attendanceListBasicDto.incrementNotRespondedConfirmations();
            }
        });
        return attendanceListBasicDto;
    }

    public List<AttendanceListUserDto> findUserAttendanceList() {
        return authService.getLoggedUser().getScoutList().stream()
            .map(this::getAttendanceListUserDtoFromScout)
            .toList();
    }

    private AttendanceListUserDto getAttendanceListUserDtoFromScout(Scout scout) {
        return new AttendanceListUserDto(
            scout.getId(),
            scout.getName(),
            scout.getSurname(),
            this.confirmationRepository.findAllByScoutId(scout.getId()).stream()
                .map(this::getAttendanceScoutInfoFromConfirmation)
                .toList()
        );
    }

    private AttendanceScoutInfoDto getAttendanceScoutInfoFromConfirmation(Confirmation confirmation) {
        return new AttendanceScoutInfoDto(
            confirmation.getEvent().getId(),
            confirmation.getEvent().getStartDate(),
            confirmation.getEvent().getEndDate(),
            confirmation.getEvent().getTitle(),
            confirmation.getAttending(),
            confirmation.getPayed(),
            confirmation.getEvent().eventAttendanceIsClosed(),
            Optional.ofNullable(confirmation.getEvent().getCloseDateTime())
                .map(dateTime -> dateTime.minusDays(3L).isBefore(ZonedDateTime.now()))
                .orElse(false)
        );
    }

    public Confirmation save(Confirmation confirmation) {
        return confirmationRepository.save(confirmation);
    }

    public Confirmation updateByUser(ConfirmationDto confirmation) {
        Confirmation confirmationDB = this.findById(confirmation.scoutId(), confirmation.eventId());
        Event event = confirmationDB.getEvent();
        if (!event.eventAttendanceIsClosed()) {
            confirmationDB.setText(confirmation.text());
            confirmationDB.setAttending(confirmation.attending());
            return confirmationRepository.save(confirmationDB);
        }
        log.warn("updateByUser - trying to edit closed confirmation");
        return confirmationDB;
    }

    public Confirmation updateByScouter(ConfirmationDto confirmationDto) {
        Confirmation confirmationDB = confirmationRepository
            .findById(new ConfirmationId(confirmationDto.scoutId(), confirmationDto.eventId()))
            .orElseThrow(WebBentayaNotFoundException::new);
        this.validateScouterScoutAccess(confirmationDB);
        confirmationDB.setText(confirmationDto.text());
        confirmationDB.setAttending(confirmationDto.attending());
        if (confirmationDB.getEvent().isActiveAttendancePayment()) {
            confirmationDB.setPayed(confirmationDto.payed() != null && confirmationDto.payed());
        } else {
            confirmationDB.setPayed(null);
        }
        return confirmationRepository.save(confirmationDB);
    }

    private void validateScouterScoutAccess(Confirmation confirmation) {
        User loggedUser = this.authService.getLoggedUser();

        Group scouterGroup = loggedUser.getGroup();
        Group eventGroup = confirmation.getEvent().getGroup();
        Group scoutGroup = confirmation.getScout().getGroup();

        if (!Objects.requireNonNull(scouterGroup).equals(eventGroup) || !scouterGroup.equals(scoutGroup)) {
            throw new WebBentayaForbiddenException("Acceso no autorizado a este scout");
        }
    }

    public void deleteAll(List<Confirmation> confirmations) {
        confirmationRepository.deleteAll(confirmations);
    }

    public void deleteAllByEventId(int eventId) {
        confirmationRepository.deleteAll(confirmationRepository.findAllByEventId(eventId));
    }

    public boolean loggedUserHasNotifications() {
        return this.authService.getLoggedUser().getScoutList().stream()
            .anyMatch(scout -> scout.getConfirmationList().stream()
                .filter(confirmation -> !confirmation.getEvent().eventAttendanceIsClosed())
                .anyMatch(confirmation -> confirmation.getAttending() == null)
            );
    }
}
