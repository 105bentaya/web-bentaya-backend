package org.scouts105bentaya.features.senior_section;

import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class SeniorFormConverter extends GenericConverter<SeniorForm, SeniorFormDto> {

    @Override
    public SeniorForm convertFromDto(SeniorFormDto dto) {
        SeniorForm seniorForm = new SeniorForm();
        seniorForm.setId(dto.id());
        seniorForm.setName(dto.name());
        seniorForm.setSurname(dto.surname());
        seniorForm.setEmail(dto.email());
        seniorForm.setPhone(dto.phone());
        seniorForm.setAcceptNewsletter(dto.acceptNewsletter());
        seniorForm.setAcceptMessageGroup(dto.acceptMessageGroup());
        seniorForm.setObservations(dto.observations());
        return seniorForm;
    }

    @Override
    public SeniorFormDto convertFromEntity(SeniorForm entity) {
        return new SeniorFormDto(
            entity.getId(),
            entity.getName(),
            entity.getSurname(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getAcceptMessageGroup(),
            entity.getAcceptNewsletter(),
            entity.getObservations()
        );
    }
}
