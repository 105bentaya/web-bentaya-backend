package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaForbiddenException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.ScoutConverter;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.ScoutStatus;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.specification.ScoutSpecification;
import org.scouts105bentaya.features.scout.specification.ScoutSpecificationFilter;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ScoutService {

    private final ScoutRepository scoutRepository;
    private final AuthService authService;
    private final UserService userService;
    private final PreScoutService preScoutService;

    public ScoutService(
        ScoutRepository scoutRepository,
        AuthService authService,
        UserService userService,
        PreScoutService preScoutService
    ) {
        this.scoutRepository = scoutRepository;
        this.authService = authService;
        this.userService = userService;
        this.preScoutService = preScoutService;
    }

    public Page<Scout> findAll(ScoutSpecificationFilter filter) {
        return scoutRepository.findAll(new ScoutSpecification(filter), filter.getPageable());
    }

    public List<Scout> findAllScoutsByLoggedScouterGroupId() {
        return scoutRepository.findScoutsByGroup(authService.getLoggedScouterGroupOrUnauthorized());
    }

    public void addUsersToNewScout(Scout scout, List<String> scoutUsers) {
        if (scoutUsers.isEmpty()) {
            return;
        }

        if (scout.getScoutType().hasScouterAccess() && scoutUsers.size() > 1) {
            throw new WebBentayaBadRequestException("Este tipo de asociada sólo puede tener un usuario asociado");
        }
        RoleEnum userRole = getScoutRole(scout.getScoutType());
        scoutUsers.forEach(username -> addUserToScout(username, scout, userRole));
    }

    public List<String> updateScoutUsers(Integer scoutId, Set<String> scoutUsers) {
        Scout scout = scoutRepository.get(scoutId);

        if (scout.getScoutType() == ScoutType.INACTIVE) {
            throw new WebBentayaConflictException("Una asociada de baja no puede tener usuarios asociados");
        } else if (scout.getScoutType().hasScouterAccess() && scoutUsers.size() > 1) {
            throw new WebBentayaBadRequestException("Este tipo de asociada sólo puede tener un usuario asociado");
        }

        scout.getAllUsers().stream()
            .filter(user -> scoutUsers.stream().noneMatch(username -> username.equalsIgnoreCase(user.getUsername())))
            .forEach(user -> {
                log.info("updateScoutUsers - removing user from scout{}", user.getUsername());
                userService.removeScoutFromUser(user, scout);
            });

        RoleEnum userRole = getScoutRole(scout.getScoutType());
        scoutUsers.forEach(username -> this.addUserToScout(username, scout, userRole));

        return scoutRepository.findScoutsUserNames(scoutId);
    }

    private void addUserToScout(String username, Scout scout, RoleEnum userRole) {
        try {
            User user = userService.findByUsername(username.toLowerCase());
            userService.addScoutToExistingUser(user, scout, userRole);
        } catch (WebBentayaUserNotFoundException ex) {
            userService.addScoutToNewUser(username.toLowerCase(), scout, userRole);
        }
    }

    private RoleEnum getScoutRole(ScoutType scoutType) {
        return switch (scoutType) {
            case SCOUT -> RoleEnum.ROLE_USER;
            case SCOUTER, COMMITTEE, MANAGER -> RoleEnum.ROLE_SCOUTER;
            case INACTIVE ->
                throw new WebBentayaForbiddenException("No se pueden añadir usuarios a una asociada de baja");
        };
    }

    public List<String> getUsersToUpdateInfo(List<String> newUsers) {
        return CollectionUtils.isEmpty(newUsers) ?
            Collections.emptyList() :
            newUsers.stream().filter(this::userDoesNotExist).toList();
    }

    private boolean userDoesNotExist(String username) {
        try {
            userService.findByUsername(username);
        } catch (WebBentayaUserNotFoundException e) {
            return true;
        }
        return false;
    }

    public Scout getPossibleInactiveScoutsFromPreScout(Integer preScoutId) {
        PreScout preScout = preScoutService.findById(preScoutId);
        if (!preScout.isHasBeenInGroup()) {
            throw new WebBentayaBadRequestException("La persona del formulario no ha estado antes en el grupo");
        }

        return scoutRepository.findFirstByPersonalDataIdDocumentNumber(preScout.getDni())
            .filter(scout -> scout.getStatus() == ScoutStatus.INACTIVE)
            .orElse(null);
    }

    public long totalPendingRegistrations() {
        ScoutSpecificationFilter scoutSpecificationFilter = new ScoutSpecificationFilter();
        scoutSpecificationFilter.setCountPerPage(0);
        scoutSpecificationFilter.setStatuses(List.of(ScoutStatus.PENDING_EXISTING, ScoutStatus.PENDING_NEW));
        return this.findAll(scoutSpecificationFilter).getTotalElements();
    }

    public Scout getFilteredScout(Integer scoutId) {
        Scout scout = this.scoutRepository.get(scoutId);
        if (authService.getLoggedUser().hasRole(RoleEnum.ROLE_USER)) {
            scout.setScoutHistory(null);
            scout.setRecordList(Collections.emptyList());
        }
        return scout;
    }
}
