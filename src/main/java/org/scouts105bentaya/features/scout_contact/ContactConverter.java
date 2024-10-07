package org.scouts105bentaya.features.scout_contact;

import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class ContactConverter extends GenericConverter<Contact, ContactDto> {

    @Override
    public Contact convertFromDto(ContactDto dto) {
        Contact contact = new Contact();
        contact.setId(dto.id());
        contact.setRelationship(dto.relationship());
        contact.setPhone(dto.phone());
        contact.setName(dto.name());
        contact.setEmail(dto.email());
        return contact;
    }

    @Override
    public ContactDto convertFromEntity(Contact entity) {
        return new ContactDto(
            entity.getId(),
            entity.getName(),
            entity.getRelationship(),
            entity.getPhone(),
            entity.getEmail()
        );
    }
}
