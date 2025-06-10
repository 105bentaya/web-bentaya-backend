package org.scouts105bentaya.features.scout.service;

import jakarta.transaction.Transactional;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.group.GroupRepository;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.dto.form.NewScoutFormDto;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.entity.EconomicData;
import org.scouts105bentaya.features.scout.entity.MedicalData;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutHistory;
import org.scouts105bentaya.features.scout.entity.ScoutRegistrationDates;
import org.scouts105bentaya.features.scout.enums.BloodType;
import org.scouts105bentaya.features.scout.enums.ScoutStatus;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoutCreationService {

    private final ScoutRepository scoutRepository;
    private final GroupRepository groupRepository;
    private final ScoutContactService scoutContactService;
    private final ScoutPersonalDataService scoutPersonalDataService;
    private final ScoutGroupDataService scoutGroupDataService;
    private final ScoutService scoutService;
    private final PreScoutService preScoutService;

    public ScoutCreationService(
        ScoutRepository scoutRepository,
        GroupRepository groupRepository,
        ScoutContactService scoutContactService,
        ScoutPersonalDataService scoutPersonalDataService,
        ScoutGroupDataService scoutGroupDataService,
        ScoutService scoutService,
        PreScoutService preScoutService
    ) {
        this.scoutRepository = scoutRepository;
        this.groupRepository = groupRepository;
        this.scoutContactService = scoutContactService;
        this.scoutPersonalDataService = scoutPersonalDataService;
        this.scoutGroupDataService = scoutGroupDataService;
        this.scoutService = scoutService;
        this.preScoutService = preScoutService;
    }

    @Transactional
    public Scout addNewScout(NewScoutFormDto form) {
        checkForPreScout(form);

        boolean contactIsDonor = form.contact().donor();
        if (contactIsDonor && form.contact().idDocument() == null) {
            throw new WebBentayaBadRequestException("Debe especificar el documento de identidad del contacto por su condición de donante");
        } else if (!contactIsDonor && form.idDocument() == null) {
            throw new WebBentayaBadRequestException("Debe especificar el documento de identidad de la persona educanda por su condición de donante");
        }

        Scout scout = new Scout();

        this.setGroupData(scout, form);

        PersonalData personalData = new PersonalData();
        personalData.setScout(scout);
        scout.setPersonalData(personalData);
        scoutPersonalDataService.updatePersonalData(PersonalDataFormDto.fromNewScoutForm(form), personalData);

        scout.setContactList(List.of(scoutContactService.newContact(form.contact(), scout)));

        EconomicData economicData = new EconomicData();
        economicData.setScout(scout);
        scout.setEconomicData(economicData);
        economicData.setIban(form.iban());
        economicData.setBank(form.bank());

        ScoutHistory scoutHistory = new ScoutHistory();
        scoutHistory.setScout(scout);
        scout.setScoutHistory(scoutHistory);

        MedicalData medicalData = new MedicalData();
        medicalData.setScout(scout);
        scout.setMedicalData(medicalData);
        medicalData.setBloodType(BloodType.NA);

        Scout savedScout = scoutRepository.save(scout);
        scoutService.addUsersToNewScout(savedScout, form.scoutUsers());
        return savedScout;
    }

    private void checkForPreScout(NewScoutFormDto form) {
        if (form.preScoutId() != null) {
            preScoutService.saveAsAssigned(form.preScoutId());
        }
    }

    private void setGroupData(Scout scout, NewScoutFormDto form) {
        scoutGroupDataService.updateScoutCensus(scout, form.census());

        scout.setScoutType(form.scoutType());
        if (scout.getScoutType() == ScoutType.INACTIVE) {
            scout.setStatus(ScoutStatus.INACTIVE);
            scout.setFederated(false);
        } else if (scout.getCensus() == null) {
            scout.setStatus(ScoutStatus.PENDING);
            scout.setFederated(false);
        } else {
            scout.setStatus(ScoutStatus.ACTIVE);
            scout.setFederated(scout.getScoutType().isScoutOrScouter());
        }

        scout.setGroup(scout.getScoutType().isScoutOrScouter() && form.groupId() != 0 ?
            groupRepository.findById(form.groupId()).orElseThrow(WebBentayaNotFoundException::new) :
            null
        );

        scout.setRegistrationDates(List.of(
            new ScoutRegistrationDates().setRegistrationDate(form.firstActivityDate()).setScout(scout)
        ));
    }
}
