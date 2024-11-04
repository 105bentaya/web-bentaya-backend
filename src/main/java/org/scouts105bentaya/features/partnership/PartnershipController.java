package org.scouts105bentaya.features.partnership;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/partnership")
public class PartnershipController {

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
