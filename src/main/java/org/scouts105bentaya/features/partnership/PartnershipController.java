package org.scouts105bentaya.features.partnership;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/partnership")
public class PartnershipController {

    private static final Logger log = LoggerFactory.getLogger(PartnershipController.class);
    private final PartnershipService partnershipService;

    public PartnershipController(PartnershipService partnershipService) {
        this.partnershipService = partnershipService;
    }

    @PostMapping("/form")
    public void sendPartnershipEmail(@RequestBody @Valid PartnershipDto partnershipDto) {
        log.info("METHOD PartnershipController.sendPartnershipEmail");
        this.partnershipService.sendPartnershipEmail(partnershipDto);
    }
}
