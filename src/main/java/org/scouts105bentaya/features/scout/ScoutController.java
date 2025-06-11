package org.scouts105bentaya.features.scout;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.dto.UserScoutDto;
import org.scouts105bentaya.features.scout.dto.form.ContactListFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicEntryFormDto;
import org.scouts105bentaya.features.scout.dto.form.MedicalDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.NewScoutFormDto;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutHistoryFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutInfoFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutRecordFormDto;
import org.scouts105bentaya.features.scout.entity.EconomicEntry;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.entity.ScoutRecord;
import org.scouts105bentaya.features.scout.enums.ScoutFileType;
import org.scouts105bentaya.features.scout.service.ScoutContactService;
import org.scouts105bentaya.features.scout.service.ScoutCreationService;
import org.scouts105bentaya.features.scout.service.ScoutEconomicDataService;
import org.scouts105bentaya.features.scout.service.ScoutFileService;
import org.scouts105bentaya.features.scout.service.ScoutGroupDataService;
import org.scouts105bentaya.features.scout.service.ScoutHistoryService;
import org.scouts105bentaya.features.scout.service.ScoutMedicalDataService;
import org.scouts105bentaya.features.scout.service.ScoutPersonalDataService;
import org.scouts105bentaya.features.scout.service.ScoutService;
import org.scouts105bentaya.features.scout.specification.ScoutSpecificationFilter;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.specification.PageDto;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/scout")
public class ScoutController {

    private final ScoutService scoutService;
    private final ScoutFileService scoutFileService;
    private final ScoutPersonalDataService scoutPersonalDataService;
    private final ScoutMedicalDataService scoutMedicalDataService;
    private final ScoutContactService scoutContactService;
    private final ScoutGroupDataService scoutGroupDataService;
    private final ScoutEconomicDataService scoutEconomicDataService;
    private final ScoutHistoryService scoutHistoryService;
    private final ScoutCreationService scoutCreationService;

    public ScoutController(
        ScoutService scoutService,
        ScoutFileService scoutFileService,
        ScoutPersonalDataService scoutPersonalDataService,
        ScoutMedicalDataService scoutMedicalDataService,
        ScoutContactService scoutContactService,
        ScoutGroupDataService scoutGroupDataService,
        ScoutEconomicDataService scoutEconomicDataService,
        ScoutHistoryService scoutHistoryService,
        ScoutCreationService scoutCreationService) {
        this.scoutService = scoutService;
        this.scoutFileService = scoutFileService;
        this.scoutPersonalDataService = scoutPersonalDataService;
        this.scoutMedicalDataService = scoutMedicalDataService;
        this.scoutContactService = scoutContactService;
        this.scoutGroupDataService = scoutGroupDataService;
        this.scoutEconomicDataService = scoutEconomicDataService;
        this.scoutHistoryService = scoutHistoryService;
        this.scoutCreationService = scoutCreationService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping
    public PageDto<ScoutDto> findAll(ScoutSpecificationFilter filter) {
        log.info("findAll - filter:{}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        return GenericConverter.convertListToPageDto(scoutService.findAll(filter), ScoutDto::fromScout);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping("/{id}")
    public ScoutDto findById(@PathVariable Integer id) {
        log.info("findById{}", SecurityUtils.getLoggedUserUsernameForLog());
        return ScoutDto.fromScout(scoutService.findById(id));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public List<UserScoutDto> findCurrentByUser() { //todo check
        log.info("METHOD ScoutController.findCurrentByUser{}", SecurityUtils.getLoggedUserUsernameForLog());
        return GenericConverter.convertEntityCollectionToDtoList(scoutService.findCurrentByUser(), UserScoutDto::fromScout);
    }

    //NEW - TODO AUTH, LOGS

    @GetMapping("/document/{id}")
    public ResponseEntity<byte[]> getScoutFile(@PathVariable Integer id) {
        return scoutFileService.downloadScoutFile(id);
    }

    @PostMapping("/document/{entityId}/{fileType}")
    public ScoutFile uploadMedicalDocument(
        @PathVariable Integer entityId,
        @PathVariable ScoutFileType fileType,
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "customName", defaultValue = "") String customName
    ) {
        return scoutFileService.createScoutFile(entityId, file, fileType, customName);
    }

    @DeleteMapping("/document/{entityId}/{fileId}/{fileType}")
    public void deletePersonalDocument(@PathVariable Integer entityId, @PathVariable Integer fileId, @PathVariable ScoutFileType fileType) {
        scoutFileService.deleteScoutFile(entityId, fileId, fileType);
    }

    @PostMapping("/new")
    public ScoutDto addNewScout(@RequestBody @Valid NewScoutFormDto newScoutFormDto) {
        return ScoutDto.fromScout(scoutCreationService.registerScout(newScoutFormDto));
    }

    @GetMapping("/last-census")
    public int getSpecialMemberLastCensus() {
        return scoutGroupDataService.findLastScoutCensus();
    }

    @GetMapping("/previous-scout/{preScoutId}")
    public ScoutDto findScoutsLikeHasBeenInGroup(@PathVariable Integer preScoutId) {
        return Optional.ofNullable(scoutService.getPossibleInactiveScoutsFromPreScout(preScoutId)).map(ScoutDto::fromScout).orElse(null);
    }

    @PatchMapping("/personal/{id}")
    public ScoutDto updatePersonalData(@PathVariable Integer id, @RequestBody @Valid PersonalDataFormDto personalDataFormDto) {
        return ScoutDto.fromScout(scoutPersonalDataService.updateScoutPersonalData(id, personalDataFormDto));
    }

    @PatchMapping("/medical/{id}")
    public ScoutDto updateMedicalData(@PathVariable Integer id, @RequestBody @Valid MedicalDataFormDto medicalDataFormDto) {
        return ScoutDto.fromScout(scoutMedicalDataService.updateMedicalData(id, medicalDataFormDto));
    }

    @PatchMapping("/contact/{id}")
    public ScoutDto updateContactData(@PathVariable Integer id, @RequestBody @Valid ContactListFormDto contactList) {
        return ScoutDto.fromScout(scoutContactService.updateScoutContactData(id, contactList.contactList()));
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

    @PatchMapping("/economic/{id}")
    public ScoutDto updateEconomicData(@PathVariable Integer id, @RequestBody @Valid EconomicDataFormDto form) {
        return ScoutDto.fromScout(scoutEconomicDataService.updateEconomicData(id, form));
    }

    @PostMapping("/economic/entry/{scoutId}")
    public EconomicEntry addDonation(
        @PathVariable Integer scoutId,
        @RequestBody @Valid EconomicEntryFormDto form
    ) {
        return scoutEconomicDataService.addEntry(scoutId, form);
    }

    @PutMapping("/economic/entry/{scoutId}/{entryId}")
    public EconomicEntry updateDonation(
        @PathVariable Integer entryId,
        @PathVariable Integer scoutId,
        @RequestBody @Valid EconomicEntryFormDto form
    ) {
        return scoutEconomicDataService.updateEntry(scoutId, entryId, form);
    }

    @DeleteMapping("/economic/entry/{scoutId}/{entryId}")
    public void deleteDonation(@PathVariable Integer entryId, @PathVariable Integer scoutId) {
        scoutEconomicDataService.deleteEntry(scoutId, entryId);
    }

    @PatchMapping("/scout-history/{id}")
    public ScoutDto updateEconomicData(@PathVariable Integer id, @RequestBody @Valid ScoutHistoryFormDto form) {
        return ScoutDto.fromScout(scoutHistoryService.updateScoutHistory(id, form));
    }
}
