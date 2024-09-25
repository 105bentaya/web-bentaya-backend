package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.ContactDto;
import org.scouts105bentaya.entity.Contact;
import org.springframework.stereotype.Component;

@Component
public class ContactConverter extends GenericConverter<Contact, ContactDto>{

    @Override
    public Contact convertFromDto(ContactDto dto) {
        Contact contact = new Contact();
        contact.setId(dto.getId());
        contact.setRelationship(dto.getRelationship());
        contact.setPhone(dto.getPhone());
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        return contact;
    }

    @Override
    public ContactDto convertFromEntity(Contact entity) {
        ContactDto dto = new ContactDto();
        dto.setId(entity.getId());
        dto.setRelationship(entity.getRelationship());
        dto.setPhone(entity.getPhone());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}
