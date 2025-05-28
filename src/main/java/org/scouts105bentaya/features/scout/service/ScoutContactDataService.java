package org.scouts105bentaya.features.scout.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.ContactFormDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutContact;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScoutContactDataService {

    private final ScoutRepository scoutRepository;
    private final ScoutService scoutService;

    public ScoutContactDataService(
        ScoutRepository scoutRepository,
        ScoutService scoutService
    ) {
        this.scoutRepository = scoutRepository;
        this.scoutService = scoutService;
    }

    public Scout updateScoutContactData(Integer id, @Valid List<ContactFormDto> contactList) {
        if (contactList == null || contactList.isEmpty() || contactList.size() > 3) {
            throw new WebBentayaBadRequestException("La lista de contactos debe contener entre 1 y 3 contactos");
        }

        Scout scout = scoutService.findById(id);

        List<ScoutContact> newContacts = new ArrayList<>();
        contactList.forEach(contactFormDto -> {
            if (contactFormDto.id() != null) {
                ScoutContact existingContact = scout.getContactList().stream()
                    .filter(contact -> contact.getId().equals(contactFormDto.id()))
                    .findFirst().orElseThrow(WebBentayaNotFoundException::new);
                this.updateExistingContact(existingContact, contactFormDto);
            } else {
                newContacts.add(this.newContact(contactFormDto, scout));
            }
        });

        scout.getContactList().removeIf(contact -> contactList.stream().noneMatch(form -> contact.getId().equals(form.id())));
        scout.getContactList().addAll(newContacts);

        return scoutRepository.save(scout);
    }

    private ScoutContact newContact(ContactFormDto contactFormDto, Scout scout) {
        ScoutContact newContact = new ScoutContact();
        updateContact(contactFormDto, newContact);
        newContact.setScout(scout);
        return newContact;
    }

    private void updateExistingContact(ScoutContact existingContact, ContactFormDto contactFormDto) {
        updateContact(contactFormDto, existingContact);
    }

    private void updateContact(ContactFormDto contactFormDto, ScoutContact contact) {
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
        contact.setEmail(contactFormDto.email());
        contact.setPhone(contactFormDto.phone());
        contact.setDonor(contactFormDto.donor());
        contact.setObservations(contactFormDto.observations());
        contact.setIdDocument(ScoutUtils.updateIdDocument(contact.getIdDocument(), contactFormDto.idDocument()));
    }
}
