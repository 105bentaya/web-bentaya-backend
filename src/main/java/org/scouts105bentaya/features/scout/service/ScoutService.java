package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaForbiddenException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class ScoutService {

    private final ScoutRepository scoutRepository;
    private final AuthService authService;
    private final UserService userService;
    private final EventService eventService;
    private final ConfirmationService confirmationService;
    private final PreScoutService preScoutService;

    public ScoutService(
        ScoutRepository scoutRepository,
        AuthService authService,
        UserService userService,
        @Lazy EventService eventService,
        ConfirmationService confirmationService,
        PreScoutService preScoutService
    ) {
        this.scoutRepository = scoutRepository;
        this.authService = authService;
        this.userService = userService;
        this.eventService = eventService;
        this.confirmationService = confirmationService;
        this.preScoutService = preScoutService;
    }

    public Page<Scout> findAll(ScoutSpecificationFilter filter) {
        return scoutRepository.findAll(new ScoutSpecification(filter), filter.getPageable());
    }

    public List<Scout> findAllByLoggedScouterGroupId() { //todo rework
        return scoutRepository.findAllNotInactiveActiveByGroup(Objects.requireNonNull(authService.getLoggedUser().getGroup()));
    }

    public Set<Scout> findCurrentByUser() {
        return authService.getLoggedUser().getScoutList();
    }

    public Scout findById(Integer id) { //add this method in repository
        return scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public void updateScoutUsers(Integer scoutId, List<String> scoutUsers) {
//        Scout scout = this.findActiveById(scoutId);
//        scout.getUserList().stream()
//            .filter(user -> scoutUsers.stream()
//                .noneMatch(username -> username.equalsIgnoreCase(user.getUsername())))
//            .forEach(user -> userService.removeScoutFromUser(user, scout));
//
//
//        scoutUsers.forEach(username -> {
//            if (scout.getUserList().stream().noneMatch(user -> user.getUsername().equalsIgnoreCase(username))) {
//                try {
//                    User user = userService.findByUsername(username.toLowerCase());
//                    log.info("updateScoutUsers - adding scout to user {}", username);
//                    userService.addScoutToExistingUser(user, scout);
//                } catch (WebBentayaUserNotFoundException ex) {
//                    log.info("updateScoutUsers - user {} does not exist, creating new user", username);
//                    userService.addScoutToNewUser(username.toLowerCase(), scout);
//                }
//            }
//        });
    }

    public void addUsersToNewScout(Scout scout, List<String> scoutUsers) {
        if (!scout.getScoutType().isScoutOrScouter() && !scoutUsers.isEmpty()) {
            throw new WebBentayaBadRequestException("No se pueden añadir usuarios a una scout de la rama indicada");
        }

        if (scout.getScoutType() == ScoutType.SCOUTER && scoutUsers.size() > 1) {
            throw new WebBentayaBadRequestException("Una scouter sólo puede tener un usuario asociado");
            // todo if user is already scouter, you cannot add another scouter
        }

        if (!scoutUsers.isEmpty()) {
            RoleEnum userRole = getScoutRole(scout.getScoutType());
            scoutUsers.forEach(username -> {
                try {
                    User user = userService.findByUsername(username.toLowerCase());
                    log.info("addUsersToNewScout - adding scout to user {}", username);
                    userService.addScoutToExistingUser(user, scout, userRole);
                } catch (WebBentayaUserNotFoundException ex) {
                    log.info("addUsersToNewScout - user {} does not exist, creating new user", username);
                    userService.addScoutToNewUser(username.toLowerCase(), scout, userRole);
                }
            });
        }
    }

    private RoleEnum getScoutRole(ScoutType scoutType) {
        return switch (scoutType) {
            case SCOUT -> RoleEnum.ROLE_USER;
            case SCOUTER, COMMITTEE, MANAGER -> RoleEnum.ROLE_SCOUTER;
            case INACTIVE -> throw new WebBentayaForbiddenException("Las scouts de baja no pueden tener usuarios");
        };
    }

    private void deleteFutureConfirmations(Scout scout) { //todo this method should be in confirmation service
        confirmationService.deleteAll(scout.getConfirmationList().stream()
            .filter(confirmation -> !confirmation.getEvent().eventHasEnded())
            .toList()
        );
    }

    private void createConfirmationForFutureEvents(Scout scout) { //todo this method should be in confirmation service
        eventService.findAllByGroup(scout.getGroup()).stream()
            .filter(event -> !event.isForScouters() && event.isActiveAttendanceList() && !event.eventHasEnded())
            .forEach(e -> {
                Confirmation confirmation = new Confirmation();
                confirmation.setScout(scout);
                confirmation.setEvent(e);
                if (e.isActiveAttendancePayment()) confirmation.setPayed(false);
                confirmationService.save(confirmation);
            });
    }

//    public ScoutFormUserUpdateDto getScoutFormUpdateUserMessage(Integer scoutId, List<String> newUsers) { //todo check
//        ScoutFormUserUpdateDto result = new ScoutFormUserUpdateDto();
//        if (scoutId != null) {
//            Scout scout = this.findActiveById(scoutId);
//            List<String> currentUsers = scout.getUserList().stream().map(User::getUsername).toList();
//            result.setAddedUsers(newUsers.stream().filter(newUser -> !currentUsers.contains(newUser)).toList());
//            result.setDeletedUsers(currentUsers.stream().filter(currentUser -> !newUsers.contains(currentUser)).toList());
//        } else {
//            result.setAddedUsers(newUsers);
//        }
//        result.setAddedNewUsers(result.getAddedUsers().stream().filter(this::userDoesNotExist).toList());
//        return result;
//    }

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

        return scoutRepository.findByPersonalDataIdDocumentNumber(preScout.getDni())
            .filter(scout -> scout.getStatus() == ScoutStatus.INACTIVE)
            .orElse(null);
    }

    public long totalPendingRegistrations() {
        ScoutSpecificationFilter scoutSpecificationFilter = new ScoutSpecificationFilter();
        scoutSpecificationFilter.setCountPerPage(0);
        scoutSpecificationFilter.setStatuses(List.of(ScoutStatus.PENDING_EXISTING, ScoutStatus.PENDING_NEW));
        return this.findAll(scoutSpecificationFilter).getTotalElements();
    }
}
