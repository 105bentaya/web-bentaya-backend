package org.scouts105bentaya.features.confirmation;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.confirmation.converter.AttendanceInfoConverter;
import org.scouts105bentaya.features.confirmation.converter.ConfirmationConverter;
import org.scouts105bentaya.features.confirmation.dto.AttendanceInfoDto;
import org.scouts105bentaya.features.confirmation.dto.AttendanceListBasicDto;
import org.scouts105bentaya.features.confirmation.dto.AttendanceListUserDto;
import org.scouts105bentaya.features.confirmation.dto.AttendanceScoutEventInfo;
import org.scouts105bentaya.features.confirmation.dto.ConfirmationDto;
import org.scouts105bentaya.features.confirmation.service.AttendanceExcelReportService;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/confirmation")
public class ConfirmationController {

    private final ConfirmationService confirmationService;
    private final ConfirmationConverter confirmationConverter;
    private final AttendanceInfoConverter attendanceInfoConverter;
    private final AttendanceExcelReportService attendanceExcelReportService;

    public ConfirmationController(
        ConfirmationService confirmationService,
        ConfirmationConverter confirmationConverter,
        AttendanceInfoConverter attendanceInfoConverter,
        AttendanceExcelReportService attendanceExcelReportService
    ) {
        this.confirmationService = confirmationService;
        this.confirmationConverter = confirmationConverter;
        this.attendanceInfoConverter = attendanceInfoConverter;
        this.attendanceExcelReportService = attendanceExcelReportService;
    }

    @PreAuthorize("hasRole('SCOUTER')")
    @GetMapping("/basic")
    public List<AttendanceListBasicDto> findLoggedScouterAttendanceList() {
        log.info("METHOD ConfirmationController.findLoggedScouterAttendanceList{}", SecurityUtils.getLoggedUserUsernameForLog());
        return confirmationService.findScouterAttendanceList();
    }

    @PreAuthorize("hasRole('SCOUTER') and @authLogic.eventIsEditableByUser(#id)")
    @GetMapping("/info/{id}")
    public List<AttendanceInfoDto> findAllByEventId(@PathVariable Integer id) {
        log.info("METHOD ConfirmationController.findAllByEventId --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return confirmationService.findAllByEventId(id).stream()
            .map(attendanceInfoConverter::convertFromEntity)
            .toList();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/notification")
    public boolean findIfLoggedUserHasNotifications() {
        return confirmationService.loggedUserHasNotifications();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public List<AttendanceListUserDto> findLoggedUserAttendanceList() {
        log.info("METHOD ConfirmationController.findLoggedUserAttendanceList{}", SecurityUtils.getLoggedUserUsernameForLog());
        return confirmationService.findUserAttendanceList();
    }

    @PreAuthorize("hasRole('USER') and @authLogic.userHasScoutId(#scoutId)")
    @GetMapping("/form/{scoutId}/{eventId}")
    public ConfirmationDto findByScoutAndEvent(@PathVariable Integer eventId, @PathVariable Integer scoutId) {
        log.info("METHOD ConfirmationController.findByScoutAndEvent --- PARAMS eventId: {}, scoutId: {}{}", eventId, scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return confirmationConverter.convertFromEntity(confirmationService.findById(scoutId, eventId));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/event/{eventId}")
    public List<AttendanceScoutEventInfo> findByEventIdForEventInfo(@PathVariable Integer eventId) {
        log.info("METHOD ConfirmationController.findByEventIdForEventInfo --- PARAMS eventId: {}{}", eventId, SecurityUtils.getLoggedUserUsernameForLog());
        return confirmationService.findByLoggedUserScoutsAndEventId(eventId);
    }

    @PreAuthorize("hasRole('SCOUTER')")
    @PutMapping("/scouter")
    public ConfirmationDto updateConfirmationByScouter(@RequestBody ConfirmationDto confirmationDto) {
        log.info("METHOD ConfirmationController.updateConfirmationByScouter --- PARAMS eventId: {}, scoutId: {}{}", confirmationDto.eventId(), confirmationDto.scoutId(), SecurityUtils.getLoggedUserUsernameForLog());
        return confirmationConverter.convertFromEntity(confirmationService.updateByScouter(confirmationDto));
    }

    @PreAuthorize("hasRole('USER') and @authLogic.userHasScoutId(#confirmationDto.scoutId)")
    @PutMapping("/user")
    public ConfirmationDto updateConfirmationByUser(@RequestBody ConfirmationDto confirmationDto) {
        log.info("METHOD ConfirmationController.updateConfirmationByUser --- PARAMS eventId: {}, scoutId: {}{}", confirmationDto.eventId(), confirmationDto.scoutId(), SecurityUtils.getLoggedUserUsernameForLog());
        return confirmationConverter.convertFromEntity(confirmationService.updateByUser(confirmationDto));
    }

    @PreAuthorize("hasRole('SCOUTER')")
    @GetMapping(value = "/courseAttendanceExcel")
    public ResponseEntity<byte[]> downloadCourseAttendanceExcelReport() {
        log.info("METHOD ConfirmationController.downloadCourseAttendanceExcelReport{}", SecurityUtils.getLoggedUserUsernameForLog());
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(this.attendanceExcelReportService.getGroupAttendanceAsExcel().toByteArray());
    }
}
