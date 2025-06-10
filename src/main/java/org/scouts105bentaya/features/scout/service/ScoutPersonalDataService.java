package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScoutPersonalDataService {

    private final ScoutRepository scoutRepository;

    public ScoutPersonalDataService(ScoutRepository scoutRepository) {
        this.scoutRepository = scoutRepository;
    }

    public Scout updateScoutPersonalData(Integer id, PersonalDataFormDto form) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        PersonalData data = scout.getPersonalData();
        this.updatePersonalData(form, data);
        return scoutRepository.save(scout);
    }

    public void updatePersonalData(PersonalDataFormDto form, PersonalData data) {
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
        data.setEmail(form.email());
        data.setShirtSize(form.shirtSize());
        data.setResidenceMunicipality(form.residenceMunicipality());
        data.setGender(form.gender());
        data.setImageAuthorization(form.imageAuthorization());

        data.setIdDocument(ScoutUtils.updateIdDocument(data.getIdDocument(), form.idDocument()));
        data.setObservations(form.observations());
    }
}
