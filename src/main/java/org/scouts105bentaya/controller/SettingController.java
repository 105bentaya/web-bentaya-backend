package org.scouts105bentaya.controller;

import org.scouts105bentaya.entity.Setting;
import org.scouts105bentaya.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.scouts105bentaya.util.SecurityUtils.getLoggedUserUsernameForLog;

@RestController
@RequestMapping("api/settings")
public class SettingController {

    private static final Logger log = LoggerFactory.getLogger(SettingController.class);
    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Setting> findAll() {
        log.info("METHOD SettingController.findAll" + getLoggedUserUsernameForLog());
        return this.settingService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{name}")
    public Setting updateValue(@RequestBody String value, @PathVariable String name) {
        log.info("METHOD SettingController.updateValue --- PARAMS value: " + value + ", name: " + name + getLoggedUserUsernameForLog());
        return this.settingService.updateValue(value, name);
    }

    @GetMapping("/get/{name}")
    public Setting findByName(@PathVariable String name) {
        return this.settingService.findByName(name);
    }
}
