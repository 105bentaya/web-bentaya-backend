package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.PreScouterDto;
import org.scouts105bentaya.entity.PreScouter;
import org.springframework.stereotype.Component;

@Component
public class PreScouterConverter extends GenericConverter<PreScouter, PreScouterDto> {

    @Override
    public PreScouter convertFromDto(PreScouterDto preScouterDto){
        PreScouter preScouter = new PreScouter();
        preScouter.setId(preScouterDto.getId());
        preScouter.setName(preScouterDto.getName());
        preScouter.setSurname(preScouterDto.getSurname());
        preScouter.setBirthday(preScouterDto.getBirthday());
        preScouter.setGender(preScouterDto.getGender());
        preScouter.setPhone(preScouterDto.getPhone());
        preScouter.setEmail(preScouterDto.getEmail());
        preScouter.setComment(preScouterDto.getComment());
        preScouter.setCreationDate(preScouterDto.getCreationDate());

        return preScouter;
    }

    @Override
    public PreScouterDto convertFromEntity(PreScouter preScouter){
        PreScouterDto preScouterDto = new PreScouterDto();
        preScouterDto.setId(preScouter.getId());
        preScouterDto.setName(preScouter.getName());
        preScouterDto.setSurname(preScouter.getSurname());
        preScouterDto.setBirthday(preScouter.getBirthday());
        preScouterDto.setGender(preScouter.getGender());
        preScouterDto.setPhone(preScouter.getPhone());
        preScouterDto.setEmail(preScouter.getEmail());
        preScouterDto.setComment(preScouter.getComment());
        preScouterDto.setCreationDate(preScouter.getCreationDate());

        return preScouterDto;
    }
}
