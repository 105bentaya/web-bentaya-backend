package org.scouts105bentaya.features.scout;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.scout.converter.ScoutConverter;
import org.scouts105bentaya.features.scout.converter.ScoutUserConverter;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutFormUserUpdateDto;
import org.scouts105bentaya.features.scout.dto.ScoutUserDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/scout")
public class ScoutController {

    private final ScoutService scoutService;
    private final ScoutConverter scoutConverter;
    private final ScoutUserConverter scoutUserConverter;

    public ScoutController(
        ScoutService scoutService,
        ScoutConverter scoutConverter,
        ScoutUserConverter scoutUserConverter
    ) {
        this.scoutService = scoutService;
        this.scoutConverter = scoutConverter;
        this.scoutUserConverter = scoutUserConverter;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping
    public List<ScoutDto> findAll() {
        log.info("METHOD ScoutController.findAll{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertEntityCollectionToDtoList(scoutService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<ScoutDto> findAllAndDisabled() {
        log.info("METHOD ScoutController.findAllAndDisabled{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertEntityCollectionToDtoList(scoutService.adminFindAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SCOUTER', 'GROUP_SCOUTER')")
    @GetMapping("/image")
    public List<ScoutDto> findAllWithoutImageAuthorization() {
        log.info("METHOD ScoutController.findAllWithoutImageAuthorization{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertEntityCollectionToDtoList(scoutService.findAllWithFalseImageAuthorization());
    }

    @PreAuthorize("hasRole('SCOUTER')")
    @GetMapping("/group")
    public List<ScoutDto> findAllByUserGroup() {
        log.info("METHOD ScoutController.findAllByUserGroup{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertEntityCollectionToDtoList(scoutService.findAllByLoggedScouterGroupId());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#id)")
    @GetMapping("/scout-form/{id}")
    public List<String> findScoutUsernames(@PathVariable Integer id) {
        log.info("METHOD ScoutController.findScoutUsernames --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutService.findScoutUsernames(id);
    }

    @PreAuthorize("#scoutId == null ? hasAnyRole('ADMIN', 'SCOUTER') : hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#scoutId)")
    @GetMapping("/scout-form-usernames")
    public ScoutFormUserUpdateDto getScoutUsernamesUpdateDto(
        @RequestParam(name = "scoutId", required = false) Integer scoutId,
        @RequestParam(name = "usernames", required = false) List<String> newUsernames
    ) {
        log.info("METHOD ScoutController.getScoutUsernamesUpdateDto --- PARAMS scoutId: {}{}", scoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutService.getScoutFormUpdateUserMessage(scoutId, newUsernames == null ? Collections.emptyList() : newUsernames);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public List<ScoutUserDto> findCurrentByUser() {
        log.info("METHOD ScoutController.findCurrentByUser{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutUserConverter.convertEntityCollectionToDtoList(scoutService.findCurrentByUser());
    }

    @PreAuthorize("hasRole('ADMIN') and #scoutDto.id == null")
    @PostMapping
    public ScoutDto save(@RequestBody ScoutDto scoutDto) {
        log.info("METHOD ScoutController.save{}", SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutService.save(scoutDto));
    }

    @PreAuthorize("hasRole('SCOUTER') and @authLogic.scouterHasGroupId(#scoutDto.group.id) and @authLogic.preScoutHasGroupId(#preScoutId, #scoutDto.group.id)")
    @PostMapping("/{preScoutId}")
    public ScoutDto saveFromPreScout(@RequestBody ScoutDto scoutDto, @PathVariable Integer preScoutId) {
        log.info("METHOD ScoutController.saveFromPreScout --- PARAMS preScoutId: {}{}", preScoutId, SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutService.saveFromPreScoutAndDelete(scoutDto, preScoutId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#scoutDto.id)")
    @PutMapping
    public ScoutDto update(@RequestBody ScoutDto scoutDto) {
        log.info("METHOD ScoutController.update --- PARAMS id: {}{}", scoutDto.id(), SecurityUtils.getLoggedUserUsernameForLog());
        return scoutConverter.convertFromEntity(scoutService.update(scoutDto));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#id)")
    @PutMapping("/scout-form/{id}")
    public void updateScoutUsers(@PathVariable Integer id, @RequestBody List<String> scoutUsers) {
        log.info("METHOD ScoutController.updateScoutUsers --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        scoutService.updateScoutUsers(id, scoutUsers);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SCOUTER') and @authLogic.userHasSameGroupIdAsScout(#id)")
    @DeleteMapping("/disable/{id}")
    public void disable(@PathVariable Integer id) {
        log.info("METHOD ScoutController.disable --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        scoutService.disable(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("METHOD ScoutController.delete --- PARAMS id: {}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        scoutService.delete(id);
    }
}
