package org.scouts105bentaya.service.impl;

import org.scouts105bentaya.dto.ConfirmationDto;
import org.scouts105bentaya.dto.attendance.AttendanceListBasicDto;
import org.scouts105bentaya.dto.attendance.AttendanceListUserDto;
import org.scouts105bentaya.dto.attendance.AttendanceScoutEventInfo;
import org.scouts105bentaya.dto.attendance.AttendanceScoutInfoDto;
import org.scouts105bentaya.entity.*;
import org.scouts105bentaya.exception.ConfirmationNotFoundException;
import org.scouts105bentaya.exception.EntityUnauthorizedAccessException;
import org.scouts105bentaya.repository.ConfirmationRepository;
import org.scouts105bentaya.service.AuthService;
import org.scouts105bentaya.service.ConfirmationService;
import org.scouts105bentaya.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfirmationServiceImpl implements ConfirmationService {

    private static final Logger log = LoggerFactory.getLogger(ConfirmationService.class);

    private final ConfirmationRepository confirmationRepository;
    private final EventService eventService;
    private final AuthService authService;

    public ConfirmationServiceImpl(ConfirmationRepository confirmationRepository, AuthService authService,
                                   @Lazy EventService eventService) {
        this.confirmationRepository = confirmationRepository;
        this.eventService = eventService;
        this.authService = authService;
    }

    @Override
    public Confirmation findById(Integer scoutId, Integer eventId) {
        return confirmationRepository.findById(new ConfirmationId(scoutId, eventId)).orElseThrow(ConfirmationNotFoundException::new);
    }

    @Override
    public List<Confirmation> findAllByEventId(Integer id) {
        return confirmationRepository.findAllByEventId(id);
    }

    @Override
    public List<AttendanceScoutEventInfo> findByLoggedUserScoutsAndEventId(Integer id) {
        List<Confirmation> confirmations = confirmationRepository.findAllByEventIdInScoutIds(id,
                this.authService.getLoggedUser().getScoutList().stream().map(Scout::getId).collect(Collectors.toList()));
        return confirmations.stream().map(this::confirmationToAttendanceScoutEventInfo).collect(Collectors.toList());
    }

    private AttendanceScoutEventInfo confirmationToAttendanceScoutEventInfo(Confirmation confirmation) {
        AttendanceScoutEventInfo dto = new AttendanceScoutEventInfo();
        dto.setAttending(confirmation.getAttending());
        dto.setScoutId(confirmation.getScout().getId());
        dto.setName(confirmation.getScout().getName());
        dto.setPayed(confirmation.getPayed());
        return dto;
    }

    @Override
    public List<AttendanceListBasicDto> findScouterAttendanceList() {
        return eventService.findAllByGroupId(authService.getLoggedUser().getGroupId()).stream()
                .filter(Event::isActiveAttendanceList)
                .map(this::getAttendanceListBasicDtoFromEvent)
                .collect(Collectors.toList());
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
            if (confirmation.getAttending() != null) {
                if (confirmation.getAttending()) {
                    attendanceListBasicDto.incrementAffirmativeConfirmations();
                    if (event.isActiveAttendancePayment() && confirmation.getPayed() != null && confirmation.getPayed()) {
                        attendanceListBasicDto.incrementAffirmativeAndPayedConfirmations();
                    }
                } else attendanceListBasicDto.incrementNegativeConfirmations();
            } else attendanceListBasicDto.incrementNotRespondedConfirmations();
        });
        return attendanceListBasicDto;
    }

    @Override
    public List<AttendanceListUserDto> findUserAttendanceList() {
        return authService.getLoggedUser().getScoutList().stream()
                .map(this::getAttendanceListUserDtoFromScout).collect(Collectors.toList());
    }

    private AttendanceListUserDto getAttendanceListUserDtoFromScout(Scout scout) {
        AttendanceListUserDto attendanceListUserDto = new AttendanceListUserDto();
        attendanceListUserDto.setScoutId(scout.getId());
        attendanceListUserDto.setName(scout.getName());
        attendanceListUserDto.setSurname(scout.getSurname());
        attendanceListUserDto.setInfo(this.confirmationRepository.findAllByScoutId(scout.getId())
                .stream().map(this::getAttendanceScoutInfoFromConfirmation)
                .collect(Collectors.toList())
        );
        return attendanceListUserDto;
    }

    private AttendanceScoutInfoDto getAttendanceScoutInfoFromConfirmation(Confirmation confirmation) {
        AttendanceScoutInfoDto result = new AttendanceScoutInfoDto();
        result.setEventId(confirmation.getEvent().getId());
        result.setEventTitle(confirmation.getEvent().getTitle());
        result.setEventStartDate(confirmation.getEvent().getStartDate());
        result.setEventEndDate(confirmation.getEvent().getEndDate());
        result.setAttending(confirmation.getAttending());
        result.setPayed(confirmation.getPayed());
        result.setClosed(confirmation.getEvent().isClosedAttendanceList() || confirmation.getEvent().eventHasEnded());
        return result;
    }

    @Override
    public Confirmation save(Confirmation confirmation) {
        return confirmationRepository.save(confirmation);
    }

    @Override
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

    @Override
    public Confirmation updateByScouter(ConfirmationDto confirmationDto) {
        Confirmation confirmationDB = confirmationRepository.findById(new ConfirmationId(confirmationDto.getScoutId(), confirmationDto.getEventId())).orElseThrow(ConfirmationNotFoundException::new);
        User loggedUser = this.authService.getLoggedUser();
        if (
                loggedUser.getGroupId().equals(confirmationDB.getEvent().getGroupId()) &&
                loggedUser.getGroupId().equals(confirmationDB.getScout().getGroupId())
        ) {
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

    @Override
    public void deleteAll(List<Confirmation> confirmations) {
        confirmationRepository.deleteAll(confirmations);
    }

    @Override
    public void deleteAllByEventId(int eventId) {
        confirmationRepository.deleteAll(confirmationRepository.findAllByEventId(eventId));
    }

    @Override
    public boolean loggedUserHasNotifications() {
        return this.authService.getLoggedUser().getScoutList().stream()
                .anyMatch(scout -> scout.getConfirmationList().stream()
                        .filter(confirmation -> !confirmation.getEvent().isClosedAttendanceList() && !confirmation.getEvent().eventHasEnded())
                        .anyMatch(confirmation -> confirmation.getAttending() == null)
                );
    }
}
