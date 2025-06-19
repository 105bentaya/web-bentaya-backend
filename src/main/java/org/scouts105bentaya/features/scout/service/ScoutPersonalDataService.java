package org.scouts105bentaya.features.scout.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.entity.Contact;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.user.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ScoutPersonalDataService {

    private final ScoutRepository scoutRepository;
    private final UserService userService;

    public ScoutPersonalDataService(ScoutRepository scoutRepository, UserService userService) {
        this.scoutRepository = scoutRepository;
        this.userService = userService;
    }

    @Transactional
    public Scout updateScoutPersonalData(Integer id, PersonalDataFormDto form) {
        Scout scout = scoutRepository.get(id);
        this.validatePersonalDataEmailsAndUsers(form, scout);
        this.updatePersonalData(form, scout.getPersonalData());
        return scoutRepository.save(scout);
    }

    private void validatePersonalDataEmailsAndUsers(PersonalDataFormDto form, Scout scout) {
        String oldEmail = scout.getPersonalData().getEmail();
        String newEmail = form.email();

        if (newEmail != null && scout.getContactList().stream().anyMatch(contact -> contact.getEmail().equals(newEmail))) {
            throw new WebBentayaConflictException("La persona asociada no puede tener el mismo correo que uno de los contactos");
        }

        if (oldEmail != null && !oldEmail.equalsIgnoreCase(newEmail)) {
            scout.getAllUsers().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(oldEmail))
                .findFirst().ifPresent(user -> userService.removeScoutFromUser(user, scout));
        }

        if (scout.getContactList().stream().noneMatch(Contact::isDonor) && form.idDocument() == null) {
            throw new WebBentayaBadRequestException("Debe especificar el documento de identidad de la asociada por su condición de donante");
        }
    }

    public void updatePersonalData(PersonalDataFormDto form, PersonalData data) {

        if (form.idDocument() != null) {
            scoutRepository.findFirstByPersonalDataIdDocumentNumber(form.idDocument().number()).ifPresent(scout -> {
                if (!scout.getId().equals(data.getScoutId())) {
                    throw new WebBentayaConflictException("Ya hay una asociada con este documento de identidad");
                }
            });
        }

        if (form.email() != null) {
            scoutRepository.findByPersonalDataEmail(form.email()).ifPresent(scout -> {
                if (!scout.getId().equals(data.getScoutId())) {
                    throw new WebBentayaConflictException("Ya hay una asociada con este correo electrónico");
                }
            });
        }

        data.setSurname(form.surname());
        data.setName(form.name());
        data.setFeltName(form.feltName());
        data.setBirthday(form.birthday());
        data.setBirthplace(form.birthplace());
        data.setBirthProvince(form.birthProvince());
        data.setNationality(form.nationality());
        data.setAddress(form.address());
        data.setCity(form.city());
        data.setProvince(form.province());
        data.setPhone(form.phone());
        data.setLandline(form.landline());
        data.setEmail(Optional.ofNullable(form.email()).map(String::toLowerCase).orElse(null));
        data.setShirtSize(form.shirtSize());
        data.setResidenceMunicipality(form.residenceMunicipality());
        data.setGender(form.gender());
        data.setImageAuthorization(form.imageAuthorization());
        data.setLargeFamily(form.largeFamily());

        data.setIdDocument(ScoutUtils.updateIdDocument(data.getIdDocument(), form.idDocument()));
        data.setObservations(form.observations());
    }
}
