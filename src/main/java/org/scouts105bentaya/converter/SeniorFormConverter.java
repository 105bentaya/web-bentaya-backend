package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.SeniorFormDto;
import org.scouts105bentaya.entity.SeniorForm;
import org.springframework.stereotype.Component;

@Component
public class SeniorFormConverter extends GenericConverter<SeniorForm, SeniorFormDto> {

    @Override
    public SeniorForm convertFromDto(SeniorFormDto dto) {
        SeniorForm seniorForm = new SeniorForm();
        seniorForm.setId(dto.getId());
        seniorForm.setName(dto.getName());
        seniorForm.setSurname(dto.getSurname());
        seniorForm.setEmail(dto.getEmail());
        seniorForm.setPhone(dto.getPhone());
        seniorForm.setAcceptNewsletter(dto.getAcceptNewsletter());
        seniorForm.setAcceptMessageGroup(dto.getAcceptMessageGroup());
        seniorForm.setObservations(dto.getObservations());
        return seniorForm;
    }

    @Override
    public SeniorFormDto convertFromEntity(SeniorForm entity) {
        SeniorFormDto seniorFormDto = new SeniorFormDto();
        seniorFormDto.setId(entity.getId());
        seniorFormDto.setName(entity.getName());
        seniorFormDto.setSurname(entity.getSurname());
        seniorFormDto.setEmail(entity.getEmail());
        seniorFormDto.setPhone(entity.getPhone());
        seniorFormDto.setAcceptNewsletter(entity.getAcceptNewsletter());
        seniorFormDto.setAcceptMessageGroup(entity.getAcceptMessageGroup());
        seniorFormDto.setObservations(entity.getObservations());
        return seniorFormDto;
    }
}
