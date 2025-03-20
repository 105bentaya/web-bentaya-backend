package org.scouts105bentaya.features.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.converter.ScoutCenterConverter;
import org.scouts105bentaya.features.booking.converter.ScoutCenterWithFilesConverter;
import org.scouts105bentaya.features.booking.dto.ScoutCenterDto;
import org.scouts105bentaya.features.booking.dto.ScoutCenterInformationDto;
import org.scouts105bentaya.features.booking.dto.ScoutCenterWithFilesDto;
import org.scouts105bentaya.features.booking.entity.ScoutCenterFile;
import org.scouts105bentaya.features.booking.repository.ScoutCenterRepository;
import org.scouts105bentaya.features.booking.service.ScoutCenterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/scout-center")
public class ScoutCenterController {

    private final ScoutCenterConverter scoutCenterConverter;
    private final ScoutCenterRepository scoutCenterRepository;
    private final ScoutCenterWithFilesConverter scoutCenterWithFilesConverter;
    private final ScoutCenterService scoutCenterService;

    public ScoutCenterController(
        ScoutCenterConverter scoutCenterConverter,
        ScoutCenterRepository scoutCenterRepository,
        ScoutCenterWithFilesConverter scoutCenterWithFilesConverter,
        ScoutCenterService scoutCenterService
    ) {
        this.scoutCenterConverter = scoutCenterConverter;
        this.scoutCenterRepository = scoutCenterRepository;
        this.scoutCenterWithFilesConverter = scoutCenterWithFilesConverter;
        this.scoutCenterService = scoutCenterService;
    }

    @GetMapping("/public")
    public List<ScoutCenterDto> getAllScoutCenters() {
        return scoutCenterConverter.convertEntityCollectionToDtoList(scoutCenterRepository.findAll());
    }

    @GetMapping("/public/info")
    public List<ScoutCenterInformationDto> getAllScoutCentersInfo() {
        return scoutCenterRepository.findAll().stream().map(ScoutCenterInformationDto::of).toList();
    }

    @GetMapping("/public/photo/{uuid}")
    public ResponseEntity<byte[]> getPublicPhoto(@PathVariable String uuid) {
        return scoutCenterService.getPhoto(uuid);
    }

    @GetMapping("/public/photos/{centerId}")
    public List<ScoutCenterFile> getScoutCentersPhotos(@PathVariable int centerId) {
        return scoutCenterRepository.get(centerId).getPhotos();
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping()
    public List<ScoutCenterWithFilesDto> getAllScoutCentersWithFiles() {
        return scoutCenterWithFilesConverter.convertEntityCollectionToDtoList(scoutCenterRepository.findAll());
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/rules/{centerId}")
    public ResponseEntity<byte[]> getRuleFile(@PathVariable int centerId) {
        return scoutCenterService.getRulePDF(centerId);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/incidences/{centerId}")
    public ResponseEntity<byte[]> getIncidenceFile(@PathVariable int centerId) {
        return scoutCenterService.getIncidenceFile(centerId);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @GetMapping("/attendance/{centerId}")
    public ResponseEntity<byte[]> getAttendanceFile(@PathVariable int centerId) {
        return scoutCenterService.getAttendanceFile(centerId);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/rules/{centerId}")
    public ScoutCenterFile uploadRuleFile(@PathVariable int centerId, @RequestPart("file") @NotNull MultipartFile file) {
        return scoutCenterService.uploadRuleFile(centerId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/incidences/{centerId}")
    public ScoutCenterFile uploadIncidencesFile(@PathVariable int centerId, @RequestPart("file") @NotNull MultipartFile file) {
        return scoutCenterService.uploadIncidenceFile(centerId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/attendance/{centerId}")
    public ScoutCenterFile uploadAttendanceFile(@PathVariable int centerId, @RequestPart("file") @NotNull MultipartFile file) {
        return scoutCenterService.uploadAttendanceFile(centerId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/main-photo/{centerId}")
    public ScoutCenterFile uploadMainPhoto(@PathVariable int centerId, @RequestPart("file") @NotNull MultipartFile file) {
        return scoutCenterService.uploadMainPhotoFile(centerId, file);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @PostMapping("/photos/{centerId}")
    public List<ScoutCenterFile> uploadPhotos(@PathVariable int centerId, @RequestPart("files") @NotEmpty List<MultipartFile> files) {
        return scoutCenterService.uploadPhotos(centerId, files);
    }

    @PreAuthorize("hasRole('SCOUT_CENTER_MANAGER')")
    @DeleteMapping("/photos/{centerId}/{photoId}")
    public void deletePhoto(@PathVariable int centerId, @PathVariable int photoId) {
        scoutCenterService.deletePhoto(centerId, photoId);
    }
}
