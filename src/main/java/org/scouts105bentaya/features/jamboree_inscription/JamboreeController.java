package org.scouts105bentaya.features.jamboree_inscription;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.jamboree_inscription.dto.JamboreeForm;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/jamboree")
public class JamboreeController {

    private final JamboreeInscriptionService jamboreeInscriptionService;

    public JamboreeController(JamboreeInscriptionService jamboreeInscriptionService) {
        this.jamboreeInscriptionService = jamboreeInscriptionService;
    }

    @PostMapping("/public/form")
    public void saveForm(@RequestBody @Valid JamboreeForm form) {
        log.info("saveForm");
        jamboreeInscriptionService.save(form);
    }

    @PreAuthorize("hasRole('FORM')")
    @GetMapping
    public ResponseEntity<byte[]> getExcel() {
        log.info("getExcel{}", SecurityUtils.getLoggedUserUsernameForLog());
        return jamboreeInscriptionService.getExcel();
    }

}
