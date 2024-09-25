package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.ConfirmationDto;
import org.scouts105bentaya.dto.attendance.AttendanceListBasicDto;
import org.scouts105bentaya.dto.attendance.AttendanceListUserDto;
import org.scouts105bentaya.dto.attendance.AttendanceScoutEventInfo;
import org.scouts105bentaya.entity.Confirmation;

import java.util.List;

public interface ConfirmationService {
    Confirmation findById(Integer scoutId, Integer eventId);

    List<Confirmation> findAllByEventId(Integer id);

    List<AttendanceScoutEventInfo> findByLoggedUserScoutsAndEventId(Integer eventId);
    List<AttendanceListBasicDto> findScouterAttendanceList();
    List<AttendanceListUserDto> findUserAttendanceList();

    Confirmation save(Confirmation confirmation);
    Confirmation updateByUser(ConfirmationDto confirmation);
    Confirmation updateByScouter(ConfirmationDto confirmation);

    void deleteAll(List<Confirmation> confirmations);
    void deleteAllByEventId(int eventId);

    boolean loggedUserHasNotifications();
}
