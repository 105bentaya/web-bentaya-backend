package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.dto.ScoutFormUserUpdateDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.shared.service.AuthService;
import org.springframework.context.annotation.Lazy;
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

    public List<Scout> findAll() {
        return scoutRepository.findAll();
    }

    public List<Scout> findAllByLoggedScouterGroupId() {
        return scoutRepository.findAllByGroupAndActiveIsTrue(Objects.requireNonNull(authService.getLoggedUser().getGroup()));
    }

    public Set<Scout> findCurrentByUser() {
        return authService.getLoggedUser().getScoutList();
    }

    public Scout findActiveById(Integer id) {
        return scoutRepository.findByIdAndActiveIsTrue(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public Scout findById(Integer id) { //add this method in repository
        return scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public void updateScoutUsers(Integer scoutId, List<String> scoutUsers) { //todo check
        Scout scout = this.findActiveById(scoutId);
        scout.getUserList().stream()
            .filter(user -> scoutUsers.stream()
                .noneMatch(username -> username.equalsIgnoreCase(user.getUsername())))
            .forEach(user -> userService.removeScoutFromUser(user, scout));


        scoutUsers.forEach(username -> {
            if (scout.getUserList().stream().noneMatch(user -> user.getUsername().equalsIgnoreCase(username))) {
                try {
                    User user = userService.findByUsername(username.toLowerCase());
                    log.info("updateScoutUsers - adding scout to user {}", username);
                    userService.addScoutToUser(user, scout);
                } catch (WebBentayaUserNotFoundException ex) {
                    log.info("updateScoutUsers - user {} does not exist, creating new user", username);
                    userService.addNewUserRoleUser(username.toLowerCase(), scout);
                }
            }
        });
    }

//    private void deleteContacts(List<Contact> currentContacts, List<Contact> newContacts) {
//        currentContacts.stream().filter(
//            contact -> newContacts.stream().noneMatch(
//                contact1 -> contact1.getId() != null && contact1.getId().equals(contact.getId()))
//        ).forEach(contactRepository::delete);
//    }

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

    public ScoutFormUserUpdateDto getScoutFormUpdateUserMessage(Integer scoutId, List<String> newUsers) { //todo check
        ScoutFormUserUpdateDto result = new ScoutFormUserUpdateDto();
        if (scoutId != null) {
            Scout scout = this.findActiveById(scoutId);
            List<String> currentUsers = scout.getUserList().stream().map(User::getUsername).toList();
            result.setAddedUsers(newUsers.stream().filter(newUser -> !currentUsers.contains(newUser)).toList());
            result.setDeletedUsers(currentUsers.stream().filter(currentUser -> !newUsers.contains(currentUser)).toList());
        } else {
            result.setAddedUsers(newUsers);
        }
        result.setAddedNewUsers(result.getAddedUsers().stream().filter(this::userDoesNotExist).toList());
        return result;
    }

    private boolean userDoesNotExist(String username) {
        try {
            userService.findByUsername(username);
        } catch (WebBentayaUserNotFoundException e) {
            return true;
        }
        return false;
    }
}
