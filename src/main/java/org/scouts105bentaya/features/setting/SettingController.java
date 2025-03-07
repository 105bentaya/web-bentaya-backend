package org.scouts105bentaya.features.setting;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Setting> findAll() {
        log.info("METHOD SettingController.findAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return this.settingService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{name}")
    public Setting updateValue(@RequestBody SettingInDto value, @PathVariable SettingEnum name) {
        log.info("METHOD SettingController.updateValue --- PARAMS value: {}, name: {}{}", value, name, SecurityUtils.getLoggedUserUsernameForLog());
        return this.settingService.updateValue(value, name);
    }

    @GetMapping("/get/{name}")
    public Setting findByName(@PathVariable SettingEnum name) {
        return this.settingService.findByName(name);
    }
}
