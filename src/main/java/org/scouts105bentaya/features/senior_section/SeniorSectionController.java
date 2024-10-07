package org.scouts105bentaya.features.senior_section;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api/senior")
public class SeniorSectionController {
    private static final Logger log = LoggerFactory.getLogger(SeniorSectionController.class);

    private final SeniorSectionService seniorSectionService;
    private final SeniorFormConverter seniorFormConverter;

    public SeniorSectionController(
        SeniorSectionService seniorSectionService,
        SeniorFormConverter seniorFormConverter
    ) {
        this.seniorSectionService = seniorSectionService;
        this.seniorFormConverter = seniorFormConverter;
    }

    @PreAuthorize("hasRole('FORM')")
    @GetMapping
    public List<SeniorFormDto> getAll() {
        log.info("METHOD getAll");
        return seniorFormConverter.convertEntityCollectionToDtoList(seniorSectionService.getAll());
    }

    @PostMapping("/form")
    public void saveForm(@RequestBody @Valid SeniorFormDto formDto) {
        log.info("METHOD saveForm");
        seniorSectionService.saveSeniorForm(formDto);
    }

    @PreAuthorize("hasRole('FORM')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("METHOD delete --- PARAMS: id: {}", id);
        seniorSectionService.delete(id);
    }
}
