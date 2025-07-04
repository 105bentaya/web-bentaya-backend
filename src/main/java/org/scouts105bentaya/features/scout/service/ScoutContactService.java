package org.scouts105bentaya.features.scout.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.ContactFormDto;
import org.scouts105bentaya.features.scout.entity.Contact;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.user.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScoutContactService {

    private final ScoutRepository scoutRepository;
    private final UserService userService;

    public ScoutContactService(
        ScoutRepository scoutRepository,
        UserService userService
    ) {
        this.scoutRepository = scoutRepository;
        this.userService = userService;
    }

    @Transactional
    public Scout updateScoutContactData(Integer id, List<ContactFormDto> contactListForm) {
        Scout scout = scoutRepository.get(id);

        this.validateContactList(contactListForm, scout);
        this.deleteUsersNoLongerInPreviousContactList(contactListForm, scout);

        List<Contact> newContacts = new ArrayList<>();
        contactListForm.forEach(contactFormDto -> {
            if (contactFormDto.id() != null) {
                Contact existingContact = scout.getContactList().stream()
                    .filter(contact -> contact.getId().equals(contactFormDto.id()))
                    .findFirst().orElseThrow(WebBentayaNotFoundException::new);
                this.updateExistingContact(existingContact, contactFormDto);
            } else {
                newContacts.add(this.newContact(contactFormDto, scout));
            }
        });

        scout.getContactList().removeIf(contact -> contactListForm.stream().noneMatch(form -> contact.getId().equals(form.id())));
        scout.getContactList().addAll(newContacts);

        return scoutRepository.save(scout);
    }

    private void deleteUsersNoLongerInPreviousContactList(List<ContactFormDto> contactListForm, Scout scout) {
        scout.getAllUsers()
            .stream().filter(user -> scout.getContactList().stream().anyMatch(oldContact -> user.getUsername().equalsIgnoreCase(oldContact.getEmail())))
            .forEach(user -> {
                if (contactListForm.stream().noneMatch(contact -> user.getUsername().equalsIgnoreCase(contact.email()))) {
                    userService.removeScoutFromUser(user, scout);
                }
            });
    }

    private void validateContactList(List<ContactFormDto> contactList, Scout scout) {
        if (contactList == null || contactList.isEmpty() || contactList.size() > 3) {
            throw new WebBentayaBadRequestException("La lista de contactos debe contener entre 1 y 3 contactos");
        }

        if (contactList.stream().filter(ContactFormDto::donor).count() > 1) {
            throw new WebBentayaBadRequestException("Sólo un contacto puede ser el donante");
        }

        Optional<ContactFormDto> donorContact = contactList.stream().filter(ContactFormDto::donor).findFirst();
        if (donorContact.isPresent()) {
            if (donorContact.get().idDocument() == null) {
                throw new WebBentayaConflictException("Debe especificar el documento de identidad del contacto donante");
            }
        } else if (scout.getPersonalData().getIdDocument() == null) {
            throw new WebBentayaConflictException("Si ningún contacto es donante, debe especificar el documento de identidad de la asociada, ya que se considera la donante");
        }

        String scoutEmail = scout.getPersonalData().getEmail();
        if (scoutEmail != null && contactList.stream().anyMatch(contact -> scoutEmail.equalsIgnoreCase(contact.email()))) {
            throw new WebBentayaConflictException("Los contactos no pueden tener el mismo correo electrónico que la persona asociada");
        }
    }

    public Contact newContact(ContactFormDto contactFormDto, Scout scout) {
        Contact newContact = new Contact();
        updateContact(contactFormDto, newContact);
        newContact.setScout(scout);
        return newContact;
    }

    private void updateExistingContact(Contact existingContact, ContactFormDto contactFormDto) {
        updateContact(contactFormDto, existingContact);
    }

    private void updateContact(ContactFormDto contactFormDto, Contact contact) {
        contact.setPersonType(contactFormDto.personType());
        if (contact.getPersonType() == PersonType.REAL) {
            contact.setCompanyName(null);
            contact.setStudies(contactFormDto.studies());
            contact.setProfession(contactFormDto.profession());
            contact.setRelationship(contactFormDto.relationship());
        } else if (contact.getPersonType() == PersonType.JURIDICAL) {
            contact.setCompanyName(contactFormDto.companyName());
            contact.setStudies(null);
            contact.setProfession(null);
            contact.setRelationship(null);
        }

        contact.setName(contactFormDto.name());
        contact.setSurname(contactFormDto.surname());
        contact.setEmail(Optional.ofNullable(contactFormDto.email()).map(String::toLowerCase).orElse(null));
        contact.setPhone(contactFormDto.phone());
        contact.setDonor(contactFormDto.donor());
        contact.setObservations(contactFormDto.observations());
        contact.setIdDocument(ScoutUtils.updateIdDocument(contact.getIdDocument(), contactFormDto.idDocument()));
    }
}
