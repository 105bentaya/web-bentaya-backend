package org.scouts105bentaya.features.setting;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.setting.dto.SettingInDto;
import org.scouts105bentaya.features.setting.dto.SettingOutDto;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<SettingOutDto> findAll() {
        log.info("findAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return this.settingService.findAll().stream().map(SettingOutDto::fromSetting).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('SCOUT_CENTER_MANAGER')")
    @PostFilter("filterObject.name.isBookingRelated")
    @GetMapping("/booking")
    public List<SettingOutDto> findAllForBooking() {
        log.info("findAllForBooking{}", SecurityUtils.getLoggedUserUsernameForLog());
        return this.settingService.findAll().stream().map(SettingOutDto::fromSetting).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUT_CENTER_MANAGER') and #name.isBookingRelated")
    @PutMapping("/{name}")
    public SettingOutDto updateValue(@RequestBody SettingInDto value, @PathVariable SettingEnum name) {
        log.info("METHOD SettingController.updateValue --- PARAMS value: {}, name: {}{}", value, name, SecurityUtils.getLoggedUserUsernameForLog());
        return this.settingService.updateValue(value, name);
    }

    @GetMapping("/get/{name}")
    public SettingOutDto findByName(@PathVariable SettingEnum name) {
        return SettingOutDto.fromSetting(this.settingService.findByName(name));
    }
}
