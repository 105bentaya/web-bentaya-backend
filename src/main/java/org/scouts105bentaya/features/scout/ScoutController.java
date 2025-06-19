package org.scouts105bentaya.features.scout;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.invoice.InvoiceService;
import org.scouts105bentaya.features.invoice.dto.InvoiceTypesDto;
import org.scouts105bentaya.features.scout.dto.EconomicDonationEntryDto;
import org.scouts105bentaya.features.scout.dto.ScoutDonorDto;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutListDataDto;
import org.scouts105bentaya.features.scout.dto.form.ContactListFormDto;
import org.scouts105bentaya.features.scout.dto.form.DonationFeeFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicEntryFormDto;
import org.scouts105bentaya.features.scout.dto.form.MedicalDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.NewScoutFormDto;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutExcelDto;
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
import org.scouts105bentaya.features.scout.service.ScoutEconomicFeeService;
import org.scouts105bentaya.features.scout.service.ScoutExcelService;
import org.scouts105bentaya.features.scout.service.ScoutFileService;
import org.scouts105bentaya.features.scout.service.ScoutGroupDataService;
import org.scouts105bentaya.features.scout.service.ScoutHistoryService;
import org.scouts105bentaya.features.scout.service.ScoutMedicalDataService;
import org.scouts105bentaya.features.scout.service.ScoutPersonalDataService;
import org.scouts105bentaya.features.scout.service.ScoutService;
import org.scouts105bentaya.features.scout.specification.DonationEntrySpecificationFilter;
import org.scouts105bentaya.features.scout.specification.ScoutSpecificationFilter;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private final InvoiceService invoiceService;
    private final ScoutConverter scoutConverter;
    private final ScoutExcelService scoutExcelService;
    private final ScoutEconomicFeeService scoutEconomicFeeService;

    public ScoutController(
        ScoutService scoutService,
        ScoutFileService scoutFileService,
        ScoutPersonalDataService scoutPersonalDataService,
        ScoutMedicalDataService scoutMedicalDataService,
        ScoutContactService scoutContactService,
        ScoutGroupDataService scoutGroupDataService,
        ScoutEconomicDataService scoutEconomicDataService,
        ScoutHistoryService scoutHistoryService,
        ScoutCreationService scoutCreationService,
        InvoiceService invoiceService,
        ScoutConverter scoutConverter,
        ScoutExcelService scoutExcelService,
        ScoutEconomicFeeService scoutEconomicFeeService
    ) {
        this.scoutService = scoutService;
        this.scoutFileService = scoutFileService;
        this.scoutPersonalDataService = scoutPersonalDataService;
        this.scoutMedicalDataService = scoutMedicalDataService;
        this.scoutContactService = scoutContactService;
        this.scoutGroupDataService = scoutGroupDataService;
        this.scoutEconomicDataService = scoutEconomicDataService;
        this.scoutHistoryService = scoutHistoryService;
        this.scoutCreationService = scoutCreationService;
        this.invoiceService = invoiceService;
        this.scoutConverter = scoutConverter;
        this.scoutExcelService = scoutExcelService;
        this.scoutEconomicFeeService = scoutEconomicFeeService;
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'SCOUTER')")
    @GetMapping
    public PageDto<ScoutListDataDto> findAll(ScoutSpecificationFilter filter) {
        log.info("findAll - filter:{}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        return GenericConverter.convertListToPageDto(scoutService.findAll(filter), ScoutListDataDto::fromScout);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'SCOUTER')")
    @PostMapping("/excel")
    public ResponseEntity<byte[]> downloadScoutExcel(@RequestBody ScoutExcelDto excelDto) {
        log.info("downloadScoutExcel - filter:{}{}", excelDto.filter(), SecurityUtils.getLoggedUserUsernameForLog());
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(scoutExcelService.downloadScoutExcel(excelDto).toByteArray());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user-edition")
    public List<ScoutListDataDto> findAllForUserEdition(ScoutSpecificationFilter filter) {
        log.info("findAllForUserEdition - filter:{}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        filter.setUnpaged();
        return GenericConverter.convertEntityCollectionToDtoList(scoutService.findAll(filter).toList(), ScoutListDataDto::fromScout);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'SCOUTER') or @authLogic.userHasAccessToScout(#scoutId)")
    @GetMapping("/{scoutId}")
    public ScoutDto findById(@PathVariable Integer scoutId) {
        log.info("findById - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutService.getFilteredScout(scoutId));
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'SCOUTER', 'TRANSACTION')")
    @GetMapping("/donation-types")
    public InvoiceTypesDto getInvoiceTypes() {
        log.info("getInvoiceTypes{}", SecurityUtils.getLoggedUserUsernameForLog());
        return invoiceService.getInvoicesTypes();
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @GetMapping("/any-pending-registrations")
    public long getTotalPendingRegistrations() {
        log.info("getTotalPendingRegistrations{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutService.totalPendingRegistrations();
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'SCOUTER')")
    @GetMapping("/new-users")
    public List<String> getNewScoutUsernames(
        @RequestParam(name = "usernames", required = false) List<String> newUsernames
    ) {
        log.info("getNewScoutUsernames{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutService.getUsersToUpdateInfo(newUsernames);
    }

    @PreAuthorize("hasRole('SECRETARY') or @authLogic.isScouterAndCanEditScout(#scoutId)")
    @PostMapping("/update-scout-users/{scoutId}")
    public List<String> updateScoutUsers(@PathVariable Integer scoutId, @RequestBody List<String> newUsernamesList) {
        log.info("updateScoutUsers - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        Set<String> newUsernames = CollectionUtils.isEmpty(newUsernamesList) ?
            Collections.emptySet() :
            new HashSet<>(newUsernamesList);
        return scoutService.updateScoutUsers(scoutId, newUsernames);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'SCOUTER') or @authLogic.isUserWithAccessToScoutFile(#scoutFileId)")
    @GetMapping("/document/{scoutFileId}")
    public ResponseEntity<byte[]> getScoutFile(@PathVariable Integer scoutFileId) {
        log.info("getScoutFile - scoutFileId:{}{}", scoutFileId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutFileService.downloadScoutFile(scoutFileId);
    }

    @PreAuthorize("hasRole('SECRETARY') or @authLogic.isScouterAndCanUploadDocument(#entityId, #fileType)")
    @PostMapping("/document/{entityId}/{fileType}")
    public ScoutFile uploadScoutFile(
        @PathVariable Integer entityId,
        @PathVariable ScoutFileType fileType,
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "customName", defaultValue = "") String customName
    ) {
        log.info("uploadScoutFile - entityId:{},fileType:{}{}", entityId, fileType, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutFileService.createScoutFile(entityId, file, fileType, customName);
    }

    @PreAuthorize("hasRole('SECRETARY') or @authLogic.isScouterAndCanUploadDocument(#entityId, #fileType)")
    @DeleteMapping("/document/{entityId}/{fileId}/{fileType}")
    public void deletePersonalDocument(@PathVariable Integer entityId, @PathVariable Integer fileId, @PathVariable ScoutFileType fileType) {
        log.info("deletePersonalDocument - entityId:{},fileId:{},fileType:{}{}", entityId, fileId, fileType, SecurityUtils.getLoggedUserUsernameForLog());
        scoutFileService.deleteScoutFile(entityId, fileId, fileType);
    }

    @PreAuthorize("hasRole('SECRETARY') or @authLogic.isScouterAndCanAddScout(#newScoutFormDto)")
    @PostMapping("/new")
    public ScoutDto addNewScout(@RequestBody @Valid NewScoutFormDto newScoutFormDto) {
        log.info("addNewScout{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutCreationService.registerScout(newScoutFormDto));
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @DeleteMapping("/pending/{scoutId}")
    public void deletePendingScout(@PathVariable Integer scoutId) {
        log.info("deletePendingScout - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        scoutCreationService.deletePendingScout(scoutId);
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @GetMapping("/last-census")
    public int getScoutLastCensus() {
        return scoutGroupDataService.findLastScoutCensus();
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @GetMapping("/last-explorer-census")
    public int getExplorerLastCensus() {
        return scoutGroupDataService.findLastExplorerCensus();
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'SCOUTER')")
    @GetMapping("/previous-scout/{preScoutId}")
    public ScoutDto findScoutsLikeHasBeenInGroup(@PathVariable Integer preScoutId) {
        log.info("findScoutsLikeHasBeenInGroup - preScoutId:{}{}", preScoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return Optional.ofNullable(scoutService.getPossibleInactiveScoutsFromPreScout(preScoutId)).map(scoutConverter::convertFromEntity).orElse(null);
    }

    @PreAuthorize("hasRole('SECRETARY') or @authLogic.isScouterAndCanEditScout(#scoutId)")
    @PatchMapping("/personal/{scoutId}")
    public ScoutDto updatePersonalData(@PathVariable Integer scoutId, @RequestBody @Valid PersonalDataFormDto personalDataFormDto) {
        log.info("updatePersonalData - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutPersonalDataService.updateScoutPersonalData(scoutId, personalDataFormDto));
    }

    @PreAuthorize("hasRole('SECRETARY') or @authLogic.isScouterAndCanEditScout(#scoutId)")
    @PatchMapping("/medical/{scoutId}")
    public ScoutDto updateMedicalData(@PathVariable Integer scoutId, @RequestBody @Valid MedicalDataFormDto medicalDataFormDto) {
        log.info("updateMedicalData - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutMedicalDataService.updateMedicalData(scoutId, medicalDataFormDto));
    }

    @PreAuthorize("hasRole('SECRETARY') or @authLogic.isScouterAndCanEditScout(#scoutId)")
    @PatchMapping("/contact/{scoutId}")
    public ScoutDto updateContactData(@PathVariable Integer scoutId, @RequestBody @Valid ContactListFormDto contactList) {
        log.info("updateContactData - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutContactService.updateScoutContactData(scoutId, contactList.contactList()));
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @PatchMapping("/scout-info/{scoutId}")
    public ScoutDto updateScoutInfo(@PathVariable Integer scoutId, @RequestBody @Valid ScoutInfoFormDto scoutInfoFormDto) {
        log.info("updateScoutInfo - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutGroupDataService.updateScoutInfo(scoutId, scoutInfoFormDto));
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @PostMapping("/scout-info/record/{scoutId}")
    public ScoutRecord addScoutRecord(@PathVariable Integer scoutId, @RequestBody @Valid ScoutRecordFormDto recordFormDto) {
        log.info("addScoutRecord - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutGroupDataService.uploadScoutRecord(scoutId, recordFormDto);
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @PutMapping("/scout-info/record/{scoutId}/{recordId}")
    public ScoutRecord updateScoutRecord(@PathVariable Integer recordId, @PathVariable Integer scoutId, @RequestBody @Valid ScoutRecordFormDto recordFormDto) {
        log.info("updateScoutRecord - scoutId:{},recordId:{}{}", scoutId, recordId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutGroupDataService.updateScoutRecord(scoutId, recordId, recordFormDto);
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @DeleteMapping("/scout-info/record/{scoutId}/{recordId}")
    public void deleteScoutRecord(@PathVariable Integer recordId, @PathVariable Integer scoutId) {
        log.info("deleteScoutRecord - scoutId:{},recordId:{}{}", scoutId, recordId, SecurityUtils.getLoggedUserUsernameForLog());
        scoutGroupDataService.deleteScoutRecord(scoutId, recordId);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'TRANSACTION') or @authLogic.isScouterAndCanEditScout(#scoutId)")
    @PatchMapping("/economic/{scoutId}")
    public ScoutDto updateEconomicData(@PathVariable Integer scoutId, @RequestBody @Valid EconomicDataFormDto form) {
        log.info("updateEconomicData - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutEconomicDataService.updateEconomicData(scoutId, form));
    }

    @PreAuthorize("hasRole('TRANSACTION')")
    @GetMapping("/economic/entries")
    public PageDto<EconomicDonationEntryDto> getEconomicDonationEntries(DonationEntrySpecificationFilter filter) {
        log.info("getEconomicDonationEntries - filter:{}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        return GenericConverter.convertListToPageDto(scoutEconomicDataService.findAllDonations(filter), EconomicDonationEntryDto::fromEntry);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'TRANSACTION')")
    @PostMapping("/economic/entry/{scoutId}")
    public EconomicEntry addDonation(
        @PathVariable Integer scoutId,
        @RequestBody @Valid EconomicEntryFormDto form
    ) {
        log.info("addDonation - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutEconomicDataService.addEntry(scoutId, form);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'TRANSACTION')")
    @GetMapping("/economic/donor/{scoutId}")
    public ScoutDonorDto getScoutDonor(@PathVariable Integer scoutId) {
        log.info("getScoutDonor - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutEconomicDataService.findDonorByScoutId(scoutId);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'TRANSACTION')")
    @PutMapping("/economic/entry/{scoutId}/{entryId}")
    public EconomicEntry updateDonation(
        @PathVariable Integer entryId,
        @PathVariable Integer scoutId,
        @RequestBody @Valid EconomicEntryFormDto form
    ) {
        log.info("updateDonation - scoutId:{},entryId:{}{}", scoutId, entryId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutEconomicDataService.updateEntry(scoutId, entryId, form);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'TRANSACTION')")
    @DeleteMapping("/economic/entry/{scoutId}/{entryId}")
    public void deleteDonation(@PathVariable Integer entryId, @PathVariable Integer scoutId) {
        log.info("deleteDonation - scoutId:{},entryId:{}{}", scoutId, entryId, SecurityUtils.getLoggedUserUsernameForLog());
        scoutEconomicDataService.deleteEntry(scoutId, entryId);
    }

    @PreAuthorize("hasAnyRole('TRANSACTION')")
    @PostMapping("/economic/new-fees")
    public void addNewFees(
        @ModelAttribute @Valid DonationFeeFormDto form,
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        log.info("addNewFee{}", SecurityUtils.getLoggedUserUsernameForLog());
        scoutEconomicFeeService.addFees(form, file);
    }

    @PreAuthorize("hasRole('SECRETARY') or @authLogic.isScouterAndCanEditGroupScout(#scoutId)")
    @PatchMapping("/scout-history/{scoutId}")
    public ScoutDto updateScoutHistory(@PathVariable Integer scoutId, @RequestBody @Valid ScoutHistoryFormDto form) {
        log.info("updateScoutHistory - scoutId:{}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutHistoryService.updateScoutHistory(scoutId, form));
    }
}
