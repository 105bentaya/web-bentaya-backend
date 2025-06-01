package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.ContactFormDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.Contact;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
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

    public Scout updateScoutContactData(Integer id, List<ContactFormDto> contactList) {
        this.validateContactList(contactList);

        Scout scout = scoutService.findById(id);

        List<Contact> newContacts = new ArrayList<>();
        contactList.forEach(contactFormDto -> {
            if (contactFormDto.id() != null) {
                Contact existingContact = scout.getContactList().stream()
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

    private void validateContactList(List<ContactFormDto> contactList) {
        if (contactList == null || contactList.isEmpty() || contactList.size() > 3) {
            throw new WebBentayaBadRequestException("La lista de contactos debe contener entre 1 y 3 contactos");
        }

        if (contactList.stream().filter(ContactFormDto::donor).count() > 1) {
            throw new WebBentayaBadRequestException("Sólo un contacto puede ser el donante");
        }
    }

    private Contact newContact(ContactFormDto contactFormDto, Scout scout) {
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
        contact.setEmail(contactFormDto.email());
        contact.setPhone(contactFormDto.phone());
        contact.setDonor(contactFormDto.donor());
        contact.setObservations(contactFormDto.observations());
        contact.setIdDocument(ScoutUtils.updateIdDocument(contact.getIdDocument(), contactFormDto.idDocument()));
    }
}
