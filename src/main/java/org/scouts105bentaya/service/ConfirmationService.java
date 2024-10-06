package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.ConfirmationDto;
import org.scouts105bentaya.dto.attendance.AttendanceListBasicDto;
import org.scouts105bentaya.dto.attendance.AttendanceListUserDto;
import org.scouts105bentaya.dto.attendance.AttendanceScoutEventInfo;
import org.scouts105bentaya.dto.attendance.AttendanceScoutInfoDto;
import org.scouts105bentaya.entity.Confirmation;
import org.scouts105bentaya.entity.ConfirmationId;
import org.scouts105bentaya.entity.Event;
import org.scouts105bentaya.entity.Scout;
import org.scouts105bentaya.entity.User;
import org.scouts105bentaya.exception.ConfirmationNotFoundException;
import org.scouts105bentaya.exception.EntityUnauthorizedAccessException;
import org.scouts105bentaya.repository.ConfirmationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfirmationService {

    private static final Logger log = LoggerFactory.getLogger(ConfirmationService.class);

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
        return confirmationRepository.findById(new ConfirmationId(scoutId, eventId))
            .orElseThrow(ConfirmationNotFoundException::new);
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
        return eventService.findAllByGroupId(authService.getLoggedUser().getGroupId()).stream()
            .filter(Event::isActiveAttendanceList)
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
        attendanceListBasicDto.setEventIsClosed(event.isClosedAttendanceList() || event.eventHasEnded());
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
            confirmation.getEvent().isClosedAttendanceList() || confirmation.getEvent().eventHasEnded()
        );
    }

    public Confirmation save(Confirmation confirmation) {
        return confirmationRepository.save(confirmation);
    }

    public Confirmation updateByUser(ConfirmationDto confirmation) {
        Confirmation confirmationDB = this.findById(confirmation.getScoutId(), confirmation.getEventId());
        Event event = confirmationDB.getEvent();
        if (!event.eventHasEnded() && !event.isClosedAttendanceList()) {
            confirmationDB.setText(confirmation.getText());
            confirmationDB.setAttending(confirmation.getAttending());
            return confirmationRepository.save(confirmationDB);
        }
        log.warn("Trying to edit closed confirmation");
        return confirmationDB;
    }

    public Confirmation updateByScouter(ConfirmationDto confirmationDto) {
        Confirmation confirmationDB = confirmationRepository.findById(new ConfirmationId(confirmationDto.getScoutId(), confirmationDto.getEventId())).orElseThrow(ConfirmationNotFoundException::new);
        User loggedUser = this.authService.getLoggedUser();
        if (loggedUser.getGroupId().equals(confirmationDB.getEvent().getGroupId()) &&
            loggedUser.getGroupId().equals(confirmationDB.getScout().getGroupId())) {
            confirmationDB.setText(confirmationDto.getText());
            confirmationDB.setAttending(confirmationDto.getAttending());
            if (confirmationDB.getEvent().isActiveAttendancePayment()) {
                confirmationDB.setPayed(confirmationDto.getPayed() != null && confirmationDto.getPayed());
            } else {
                confirmationDB.setPayed(null);
            }
            return confirmationRepository.save(confirmationDB);
        }
        throw new EntityUnauthorizedAccessException("Acceso no autorizado a este scout");
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
                .filter(confirmation -> !confirmation.getEvent().isClosedAttendanceList() && !confirmation.getEvent().eventHasEnded())
                .anyMatch(confirmation -> confirmation.getAttending() == null)
            );
    }
}
