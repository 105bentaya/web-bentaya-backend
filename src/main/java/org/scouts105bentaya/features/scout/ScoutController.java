package org.scouts105bentaya.features.scout;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.scout.converter.ScoutConverter;
import org.scouts105bentaya.features.scout.converter.ScoutUserConverter;
import org.scouts105bentaya.features.scout.dto.OldScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutFormUserUpdateDto;
import org.scouts105bentaya.features.scout.dto.ScoutUserDto;
import org.scouts105bentaya.features.scout.dto.form.ContactListFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.MedicalDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutInfoFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutRecordFormDto;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.entity.ScoutRecord;
import org.scouts105bentaya.features.scout.service.ScoutContactDataService;
import org.scouts105bentaya.features.scout.service.ScoutEconomicDataService;
import org.scouts105bentaya.features.scout.service.ScoutFileService;
import org.scouts105bentaya.features.scout.service.ScoutGroupDataService;
import org.scouts105bentaya.features.scout.service.ScoutMedicalDataService;
import org.scouts105bentaya.features.scout.service.ScoutPersonalDataService;
import org.scouts105bentaya.features.scout.service.ScoutService;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/scout")
public class ScoutController {

    private final ScoutService scoutService;
    private final ScoutConverter scoutConverter;
    private final ScoutUserConverter scoutUserConverter;
    private final ScoutFileService scoutFileService;
    private final ScoutPersonalDataService scoutPersonalDataService;
    private final ScoutMedicalDataService scoutMedicalDataService;
    private final ScoutContactDataService scoutContactDataService;
    private final ScoutGroupDataService scoutGroupDataService;
    private final ScoutEconomicDataService scoutEconomicDataService;

    public ScoutController(
        ScoutService scoutService,
        ScoutConverter scoutConverter,
        ScoutUserConverter scoutUserConverter,
        ScoutFileService scoutFileService,
        ScoutPersonalDataService scoutPersonalDataService,
        ScoutMedicalDataService scoutMedicalDataService,
        ScoutContactDataService scoutContactDataService,
        ScoutGroupDataService scoutGroupDataService,
        ScoutEconomicDataService scoutEconomicDataService
    ) {
        this.scoutService = scoutService;
        this.scoutConverter = scoutConverter;
        this.scoutUserConverter = scoutUserConverter;
        this.scoutFileService = scoutFileService;
        this.scoutPersonalDataService = scoutPersonalDataService;
        this.scoutMedicalDataService = scoutMedicalDataService;
        this.scoutContactDataService = scoutContactDataService;
        this.scoutGroupDataService = scoutGroupDataService;
        this.scoutEconomicDataService = scoutEconomicDataService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping
    public List<OldScoutDto> findAll() {
        log.info("METHOD ScoutController.findAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertEntityCollectionToDtoList(scoutService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<OldScoutDto> findAllAndDisabled() {
        log.info("METHOD ScoutController.findAllAndDisabled{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertEntityCollectionToDtoList(scoutService.adminFindAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping("/{id}")
    public ScoutDto findById(@PathVariable Integer id) {
        log.info("findById{}", SecurityUtils.getLoggedUserUsernameForLog());
        return ScoutDto.fromScout(scoutService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping("/image")
    public List<OldScoutDto> findAllWithoutImageAuthorization() {
        log.info("METHOD ScoutController.findAllWithoutImageAuthorization{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertEntityCollectionToDtoList(scoutService.findAllWithFalseImageAuthorization());
    }

    @PreAuthorize("hasRole('SCOUTER')")
    @GetMapping("/group")
    public List<OldScoutDto> findAllByUserGroup() {
        log.info("METHOD ScoutController.findAllByUserGroup{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertEntityCollectionToDtoList(scoutService.findAllByLoggedScouterGroupId());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#id)")
    @GetMapping("/scout-form/{id}")
    public List<String> findScoutUsernames(@PathVariable Integer id) {
        log.info("METHOD ScoutController.findScoutUsernames --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutService.findScoutUsernames(id);
    }

    @PreAuthorize("#scoutId == null ? hasAnyRole('ADMIN', 'SCOUTER') : hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#scoutId)")
    @GetMapping("/scout-form-usernames")
    public ScoutFormUserUpdateDto getScoutUsernamesUpdateDto(
        @RequestParam(name = "scoutId", required = false) Integer scoutId,
        @RequestParam(name = "usernames", required = false) List<String> newUsernames
    ) {
        log.info("METHOD ScoutController.getScoutUsernamesUpdateDto --- PARAMS scoutId: {}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutService.getScoutFormUpdateUserMessage(scoutId, newUsernames == null ? Collections.emptyList() : newUsernames);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public List<ScoutUserDto> findCurrentByUser() {
        log.info("METHOD ScoutController.findCurrentByUser{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutUserConverter.convertEntityCollectionToDtoList(scoutService.findCurrentByUser());
    }

    @PreAuthorize("hasRole('ADMIN') and #oldScoutDto.id == null")
    @PostMapping
    public OldScoutDto save(@RequestBody OldScoutDto oldScoutDto) {
        log.info("METHOD ScoutController.save{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutService.save(oldScoutDto));
    }

    @PreAuthorize("hasRole('SCOUTER') and @authLogic.scouterHasGroupId(#oldScoutDto.group.id) and @authLogic.preScoutHasGroupId(#preScoutId, #oldScoutDto.group.id)")
    @PostMapping("/{preScoutId}")
    public OldScoutDto saveFromPreScout(@RequestBody OldScoutDto oldScoutDto, @PathVariable Integer preScoutId) {
        log.info("METHOD ScoutController.saveFromPreScout --- PARAMS preScoutId: {}{}", preScoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutService.saveFromPreScoutAndDelete(oldScoutDto, preScoutId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#oldScoutDto.id)")
    @PutMapping
    public OldScoutDto update(@RequestBody OldScoutDto oldScoutDto) {
        log.info("METHOD ScoutController.update --- PARAMS id: {}{}", oldScoutDto.id(), SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutService.update(oldScoutDto));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#id)")
    @PutMapping("/scout-form/{id}")
    public void updateScoutUsers(@PathVariable Integer id, @RequestBody List<String> scoutUsers) {
        log.info("METHOD ScoutController.updateScoutUsers --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        scoutService.updateScoutUsers(id, scoutUsers);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#id)")
    @DeleteMapping("/disable/{id}")
    public void disable(@PathVariable Integer id) {
        log.info("METHOD ScoutController.disable --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        scoutService.disable(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("METHOD ScoutController.delete --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        scoutService.delete(id);
    }

    //NEW - TODO AUTH, LOGS

    @GetMapping("/document/{id}")
    public ResponseEntity<byte[]> getScoutFile(@PathVariable Integer id) {
        return scoutFileService.downloadScoutFile(id);
    }

    @PatchMapping("/personal/{id}")
    public ScoutDto updatePersonalData(@PathVariable Integer id, @RequestBody @Valid PersonalDataFormDto personalDataFormDto) {
        return ScoutDto.fromScout(scoutPersonalDataService.updatePersonalData(id, personalDataFormDto));
    }

    @PostMapping("/personal/docs/{id}")
    public ScoutFile uploadPersonalDocument(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        return scoutPersonalDataService.uploadPersonalDataFile(id, file);
    }

    @DeleteMapping("/personal/docs/{scoutId}/{fileId}")
    public void deletePersonalDocument(@PathVariable Integer scoutId, @PathVariable Integer fileId) {
        scoutPersonalDataService.deletePersonalDataFile(scoutId, fileId);
    }

    @PatchMapping("/medical/{id}")
    public ScoutDto updateMedicalData(@PathVariable Integer id, @RequestBody @Valid MedicalDataFormDto medicalDataFormDto) {
        return ScoutDto.fromScout(scoutMedicalDataService.updateMedicalData(id, medicalDataFormDto));
    }

    @PostMapping("/medical/docs/{id}")
    public ScoutFile uploadMedicalDocument(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        return scoutMedicalDataService.uploadMedicalDataFile(id, file);
    }

    @DeleteMapping("/medical/docs/{scoutId}/{fileId}")
    public void deleteMedicalDocument(@PathVariable Integer scoutId, @PathVariable Integer fileId) {
        scoutMedicalDataService.deleteMedicalDataFile(scoutId, fileId);
    }

    @PatchMapping("/contact/{id}")
    public ScoutDto updateContactData(@PathVariable Integer id, @RequestBody @Valid ContactListFormDto contactList) {
        return ScoutDto.fromScout(scoutContactDataService.updateScoutContactData(id, contactList.contactList()));
    }

    @PatchMapping("/scout-info/{id}")
    public ScoutDto updateScoutInfo(@PathVariable Integer id, @RequestBody @Valid ScoutInfoFormDto scoutInfoFormDto) {
        return ScoutDto.fromScout(scoutGroupDataService.updateScoutInfo(id, scoutInfoFormDto));
    }

    @PostMapping("/scout-info/record/{scoutId}")
    public ScoutRecord addScoutRecord(@PathVariable Integer scoutId, @RequestBody @Valid ScoutRecordFormDto recordFormDto) {
        return scoutGroupDataService.uploadScoutRecord(scoutId, recordFormDto);
    }

    @PutMapping("/scout-info/record/{scoutId}/{recordId}")
    public ScoutRecord updateScoutRecord(@PathVariable Integer recordId, @PathVariable Integer scoutId, @RequestBody @Valid ScoutRecordFormDto recordFormDto) {
        return scoutGroupDataService.updateScoutRecord(scoutId, recordId, recordFormDto);
    }

    @DeleteMapping("/scout-info/record/{scoutId}/{recordId}")
    public void deleteScoutRecord(@PathVariable Integer recordId, @PathVariable Integer scoutId) {
        scoutGroupDataService.deleteScoutRecord(scoutId, recordId);
    }

    @PostMapping("/scout-info/record-documents/{recordId}")
    public ScoutFile uploadRecordDocument(@PathVariable Integer recordId, @RequestParam("file") MultipartFile file) {
        return scoutGroupDataService.uploadRecordFile(recordId, file);
    }

    @DeleteMapping("/scout-info/record-documents/{recordId}/{fileId}")
    public void deleteRecordDocument(@PathVariable Integer recordId, @PathVariable Integer fileId) {
        scoutGroupDataService.deleteRecordFile(recordId, fileId);
    }

    @PatchMapping("/economic/{id}")
    public ScoutDto updateEconomicData(@PathVariable Integer id, @RequestBody @Valid EconomicDataFormDto form) {
        return ScoutDto.fromScout(scoutEconomicDataService.updateEconomicData(id, form));
    }

    @PostMapping("/economic/docs/{id}")
    public ScoutFile uploadEconomicDocument(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        return scoutEconomicDataService.uploadEconomicDataFile(id, file);
    }

    @DeleteMapping("/economic/docs/{scoutId}/{fileId}")
    public void deleteEconomicDocument(@PathVariable Integer scoutId, @PathVariable Integer fileId) {
        scoutEconomicDataService.deleteEconomicDataFile(scoutId, fileId);
    }
}
