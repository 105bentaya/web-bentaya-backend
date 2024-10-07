package org.scouts105bentaya.features.pre_scouter;

import org.scouts105bentaya.shared.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/pre-scouter")
public class PreScouterController {

    private static final Logger log = LoggerFactory.getLogger(PreScouterController.class);
    private final PreScouterService preScouterService;
    private final PreScouterConverter preScouterConverter;

    public PreScouterController(
        PreScouterService preScouterService,
        PreScouterConverter preScouterConverter
    ) {
        this.preScouterService = preScouterService;
        this.preScouterConverter = preScouterConverter;
    }

    @PreAuthorize("hasRole('FORM')")
    @GetMapping
    public List<PreScouterDto> findAll() {
        log.info("METHOD PreScouterController.findAll{}", SecurityUtils.getLoggedUserUsernameForLog());
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
        log.info("METHOD PreScouterController.getPDF --- PARAMS id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return this.preScouterService.getPDF(id);
    }

    @PreAuthorize("hasRole('FORM')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("METHOD PreScouterController.delete --- PARAMS id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        this.preScouterService.delete(id);
    }
}
