package org.scouts105bentaya.features.scout_center;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.scout_center.dto.ScoutCenterDto;
import org.scouts105bentaya.features.scout_center.dto.ScoutCenterInformationDto;
import org.scouts105bentaya.features.scout_center.dto.ScoutCenterWithFilesDto;
import org.scouts105bentaya.features.scout_center.entity.ScoutCenterFile;
import org.scouts105bentaya.features.scout_center.repository.ScoutCenterRepository;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/scout-center")
public class ScoutCenterController {

    private final ScoutCenterRepository scoutCenterRepository;
    private final ScoutCenterService scoutCenterService;

    public ScoutCenterController(
        ScoutCenterRepository scoutCenterRepository,
        ScoutCenterService scoutCenterService
    ) {
        this.scoutCenterRepository = scoutCenterRepository;
        this.scoutCenterService = scoutCenterService;
    }

    @GetMapping("/public")
    public List<ScoutCenterDto> getAllScoutCenters() {
        log.info("getAllScoutCenters");
        return scoutCenterRepository.findAll().stream().map(ScoutCenterDto::of).toList();
    }

    @GetMapping("/public/info")
    public List<ScoutCenterInformationDto> getAllScoutCentersInfo() {
        log.info("getAllScoutCentersInfo");
        return scoutCenterRepository.findAll().stream().map(ScoutCenterInformationDto::of).toList();
    }

    @GetMapping("/public/photo/{uuid}")
    public ResponseEntity<byte[]> getPublicPhoto(@PathVariable String uuid) {
        log.info("getPublicPhoto {}", uuid);
        return scoutCenterService.getPhoto(uuid);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping
    public List<ScoutCenterWithFilesDto> getAllScoutCentersWithFiles() {
        log.info("getAllScoutCentersWithFiles{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterRepository.findAll().stream().map(ScoutCenterWithFilesDto::of).toList();
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/{centerId}")
    public ScoutCenterDto updateScoutCenter(@PathVariable Integer centerId, @RequestBody @Valid ScoutCenterDto scoutCenterDto) {
        log.info("updateScoutCenter {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return ScoutCenterDto.of(scoutCenterService.updateScoutCenter(centerId, scoutCenterDto));
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userHasAccessToScoutCenter(#centerId)")
    @GetMapping("/rules/{centerId}")
    public ResponseEntity<byte[]> getRuleFile(@PathVariable int centerId) {
        log.info("getRuleFile {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterService.getRulePDF(centerId).asResponseEntity();
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userHasAccessToScoutCenter(#centerId)")
    @GetMapping("/incidences/{centerId}")
    public ResponseEntity<byte[]> getIncidenceFile(@PathVariable int centerId) {
        log.info("getIncidenceFile {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterService.getIncidenceFile(centerId).asResponseEntity();
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER') or hasRole('SCOUT_CENTER_REQUESTER') and @authLogic.userHasAccessToScoutCenter(#centerId)")
    @GetMapping("/attendance/{centerId}")
    public ResponseEntity<byte[]> getAttendanceFile(@PathVariable int centerId) { //todo, same file for every booking, tfg 14
        log.info("getAttendanceFile {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterService.getAttendanceFile(centerId).asResponseEntity();
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/rules/{centerId}")
    public ScoutCenterFile uploadRuleFile(@PathVariable int centerId, @RequestPart("file") @NotNull MultipartFile file) {
        log.info("uploadRuleFile {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterService.uploadRuleFile(centerId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/incidences/{centerId}")
    public ScoutCenterFile uploadIncidencesFile(@PathVariable int centerId, @RequestPart("file") @NotNull MultipartFile file) {
        log.info("uploadIncidencesFile {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterService.uploadIncidenceFile(centerId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/attendance/{centerId}")
    public ScoutCenterFile uploadAttendanceFile(@PathVariable int centerId, @RequestPart("file") @NotNull MultipartFile file) {
        log.info("uploadAttendanceFile {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterService.uploadAttendanceFile(centerId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/main-photo/{centerId}")
    public ScoutCenterFile uploadMainPhoto(@PathVariable int centerId, @RequestPart("file") @NotNull MultipartFile file) {
        log.info("uploadMainPhoto {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterService.uploadMainPhotoFile(centerId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/photos/{centerId}")
    public List<ScoutCenterFile> uploadPhotos(@PathVariable int centerId, @RequestPart("files") @NotEmpty List<MultipartFile> files) {
        log.info("uploadPhotos {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutCenterService.uploadPhotos(centerId, files);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @DeleteMapping("/photos/{centerId}/{photoId}")
    public void deletePhoto(@PathVariable int centerId, @PathVariable int photoId) {
        log.info("deletePhoto {}{}", centerId, SecurityUtils.getLoggedUserUsernameForLog());
        scoutCenterService.deletePhoto(centerId, photoId);
    }
}
