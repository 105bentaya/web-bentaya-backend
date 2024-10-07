package org.scouts105bentaya.features.pre_scout;

import jakarta.validation.Valid;
import org.scouts105bentaya.core.exception.PdfCreationException;
import org.scouts105bentaya.features.pre_scout.dto.PreScoutAssignationDto;
import org.scouts105bentaya.features.pre_scout.dto.PreScoutDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/pre-scout")
public class PreScoutController {

    private static final Logger log = LoggerFactory.getLogger(PreScoutController.class);
    private final PreScoutService preScoutService;
    private final PreScoutConverter preScoutConverter;

    public PreScoutController(
        PreScoutService preScoutService,
        PreScoutConverter preScoutConverter
    ) {
        this.preScoutService = preScoutService;
        this.preScoutConverter = preScoutConverter;
    }

    @PreAuthorize("hasRole('FORM')")
    @GetMapping
    public List<PreScoutDto> findAll() {
        log.info("METHOD PreScoutController.findAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return this.preScoutConverter.convertEntityCollectionToDtoList(this.preScoutService.findAll());
    }

    @PreAuthorize("hasRole('SCOUTER')")
    @GetMapping("/assignation")
    public List<PreScoutDto> findAllAssignedByGroupId() {
        log.info("METHOD PreScoutController.findAllAssignedByGroupId{}", SecurityUtils.getLoggedUserUsernameForLog());
        return this.preScoutConverter.convertEntityCollectionToDtoList(this.preScoutService.findAllAssignedByLoggedScouter());
    }

    @PreAuthorize("hasRole('FORM')")
    @PostMapping("/assignation")
    public void saveAssignation(@RequestBody PreScoutAssignationDto preScoutAssignationDto) {
        log.info("METHOD PreScoutController.saveAssignation --- PARAMS id:{}{}", preScoutAssignationDto.preScoutId(), SecurityUtils.getLoggedUserUsernameForLog());
        this.preScoutService.saveAssignation(preScoutAssignationDto);
    }

    //todo move status check to own class; userHasGroupId shouldnt only be used with the dto group id, it should also be used with the entity groupId to prevent illegal group changes
    @PreAuthorize("hasRole('FORM') or hasRole('SCOUTER') and #dto.status >= 0 and #dto.status <= 3 and @authLogic.userHasGroupId(#dto.groupId)")
    @PutMapping("/assignation")
    public void updateAssignation(@RequestBody PreScoutAssignationDto dto) {
        log.info("METHOD PreScoutController.updateAssignation --- PARAMS id:{}{}", dto.preScoutId(), SecurityUtils.getLoggedUserUsernameForLog());
        this.preScoutService.updateAssignation(dto);
    }

    @PostMapping("/form")
    public void sendAndSavePreScoutForm(@RequestBody @Valid PreScoutDto preScoutDto) {
        log.info("METHOD PreScoutController.sendAndSavePreScoutForm");
        this.preScoutService.saveAndSendEmail(preScoutDto);
    }

    @PreAuthorize("hasRole('FORM') or hasRole('SCOUTER') and @authLogic.userHasPreScoutId(#id)")
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> getPDF(@PathVariable Integer id) {
        log.info("METHOD PreScoutController.getPDF --- PARAMS id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return this.preScoutService.getPDF(id);
    }

    @PreAuthorize("hasRole('FORM')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("METHOD PreScoutController.delete --- PARAMS id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        this.preScoutService.delete(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PdfCreationException.class)
    public Map<String, String> handlePdfException() {
        return Map.of("message", "Error al generar el pdf");
    }
}
