package org.scouts105bentaya.features.scout.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.dto.OldScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutFormUserUpdateDto;
import org.scouts105bentaya.features.scout.entity.Scout;
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
        return scoutRepository.findAllByActiveIsTrue();
    }

    public List<Scout> adminFindAll() {
        return scoutRepository.findAll();
    }

    public List<Scout> findAllWithFalseImageAuthorization() {
        return scoutRepository.findAllByImageAuthorizationAndActiveIsTrue(false);
    }

    public List<Scout> findAllByLoggedScouterGroupId() {
        return scoutRepository.findAllByGroupAndActiveIsTrue(Objects.requireNonNull(authService.getLoggedUser().getGroup()));
    }

    public List<String> findScoutUsernames(Integer id) {
        Scout scout = this.findActiveById(id);
        return scout.getUserList().stream().map(User::getUsername).toList();
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

    public Scout save(OldScoutDto oldScoutDto) {
//        OldScout scoutToSave = scoutConverter.convertFromDto(oldScoutDto);
//        scoutToSave.setEnabled(true);
//        OldScout savedScout = scoutRepository.save(scoutToSave);
//        savedScout.getContactList().forEach(contact -> {
//            contact.setScout(savedScout);
//            contactRepository.save(contact);
//        });
//        this.createConfirmationForFutureEvents(savedScout);
//        return savedScout;
        return null;
    }

    public Scout saveFromPreScoutAndDelete(OldScoutDto oldScoutDto, Integer preScoutId) {
        Scout scout = save(oldScoutDto);
        this.preScoutService.saveAsAssigned(preScoutId);
        return scout;
    }

    public Scout update(OldScoutDto oldScoutDto) {
//        OldScout scoutToUpdate = scoutConverter.convertFromDto(oldScoutDto);
//        OldScout scoutDB = this.findActiveById(oldScoutDto.id());
//        boolean hasChangedGroup = !scoutDB.getGroup().getId().equals(scoutToUpdate.getGroup().getId());
//
//        scoutDB.setDni(scoutToUpdate.getDni());
//        scoutDB.setName(scoutToUpdate.getName());
//        scoutDB.setMedicalData(scoutToUpdate.getMedicalData());
//        scoutDB.setGroup(scoutToUpdate.getGroup());
//        scoutDB.setBirthday(scoutToUpdate.getBirthday());
//        scoutDB.setSurname(scoutToUpdate.getSurname());
//        scoutDB.setGender(scoutToUpdate.getGender());
//        scoutDB.setShirtSize(scoutToUpdate.getShirtSize());
//        scoutDB.setMunicipality(scoutToUpdate.getMunicipality());
//        scoutDB.setCensus(scoutToUpdate.getCensus());
//        scoutDB.setObservations(scoutToUpdate.getObservations());
//        scoutDB.setImageAuthorization(scoutToUpdate.isImageAuthorization());
//        scoutDB.setProgressions(scoutToUpdate.getProgressions());
//
//        this.deleteContacts(scoutDB.getContactList(), scoutToUpdate.getContactList());
//
//        scoutDB.setContactList(scoutToUpdate.getContactList());
//        scoutDB.getContactList().forEach(contact -> contact.setScout(scoutDB));
//
//        OldScout savedScout = scoutRepository.save(scoutDB);
//
//        if (hasChangedGroup) {
//            this.deleteFutureConfirmations(savedScout);
//            this.createConfirmationForFutureEvents(scoutDB);
//        }
//
//        return savedScout;
        return null;
    }

    public void updateScoutUsers(Integer scoutId, List<String> scoutUsers) {
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

    public ScoutFormUserUpdateDto getScoutFormUpdateUserMessage(Integer scoutId, List<String> newUsers) {
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

    @Transactional
    public void disable(Integer id) {
        Scout scout = this.findActiveById(id);
        scout.getUserList().forEach(user -> userService.removeScoutFromUser(user, scout));
        this.deleteFutureConfirmations(scout);
        scout.setActive(false);
        scoutRepository.save(scout);
    }

    @Transactional
    public void delete(Integer id) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        scout.getUserList().forEach(user -> userService.removeScoutFromUser(user, scout));
        scoutRepository.deleteById(id);
    }
}
