package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.PreScouterDto;
import org.scouts105bentaya.entity.PreScouter;
import org.springframework.stereotype.Component;

@Component
public class PreScouterConverter extends GenericConverter<PreScouter, PreScouterDto> {

    @Override
    public PreScouter convertFromDto(PreScouterDto preScouterDto){
        PreScouter preScouter = new PreScouter();
        preScouter.setId(preScouterDto.id());
        preScouter.setName(preScouterDto.name());
        preScouter.setSurname(preScouterDto.surname());
        preScouter.setBirthday(preScouterDto.birthday());
        preScouter.setGender(preScouterDto.gender());
        preScouter.setPhone(preScouterDto.phone());
        preScouter.setEmail(preScouterDto.email());
        preScouter.setComment(preScouterDto.comment());
        preScouter.setCreationDate(preScouterDto.creationDate());

        return preScouter;
    }

    @Override
    public PreScouterDto convertFromEntity(PreScouter preScouter){
        return new PreScouterDto(
            preScouter.getId(),
            preScouter.getName(),
            preScouter.getSurname(),
            preScouter.getBirthday(),
            preScouter.getGender(),
            preScouter.getPhone(),
            preScouter.getEmail(),
            preScouter.getComment(),
            preScouter.getCreationDate()
        );
    }
}
