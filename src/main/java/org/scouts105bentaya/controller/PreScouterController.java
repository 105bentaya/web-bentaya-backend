package org.scouts105bentaya.controller;

import org.scouts105bentaya.converter.PreScouterConverter;
import org.scouts105bentaya.dto.PreScouterDto;
import org.scouts105bentaya.service.PreScouterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.scouts105bentaya.util.SecurityUtils.getLoggedUserUsernameForLog;

@RestController
@RequestMapping("api/pre-scouter")
public class PreScouterController {

    private static final Logger log = LoggerFactory.getLogger(PreScouterController.class);
    private final PreScouterService preScouterService;
    private final PreScouterConverter preScouterConverter;

    public PreScouterController(PreScouterService preScouterService, PreScouterConverter preScouterConverter) {
        this.preScouterService = preScouterService;
        this.preScouterConverter = preScouterConverter;
    }

    @PreAuthorize("hasRole('FORM')")
    @GetMapping
    public List<PreScouterDto> findAll() {
        log.info("METHOD PreScouterController.findAll" + getLoggedUserUsernameForLog());
        return this.preScouterConverter.convertEntityCollectionToDtoList(this.preScouterService.findAll());
    }

    @PostMapping("/form")
    public void saveAndSendPreScouterForm(@RequestBody PreScouterDto preScouterDto) {
        log.info("METHOD PreScouterController.saveAndSendPreScouterForm");
        this.preScouterService.saveAndSendEmail(preScouterDto);
    }

    @PreAuthorize("hasRole('FORM')")
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> getPDF(@PathVariable Integer id) {
        log.info("METHOD PreScouterController.getPDF --- PARAMS id:" + id + getLoggedUserUsernameForLog());
        return this.preScouterService.getPDF(id);
    }

    @PreAuthorize("hasRole('FORM')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("METHOD PreScouterController.delete --- PARAMS id:" + id + getLoggedUserUsernameForLog());
        this.preScouterService.delete(id);
    }
}
