package org.scouts105bentaya.features.scout.service;

import jakarta.transaction.Transactional;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.group.GroupRepository;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
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
    public Scout registerScout(NewScoutFormDto form) {
        this.validate(form);
        Scout scout = form.preScoutId() != null ?
            this.addNewScoutFromPreScout(form) :
            this.createNewScout(form);

        Scout savedScout = scoutRepository.save(scout);
        scoutService.addUsersToNewScout(savedScout, form.scoutUsers());
        return savedScout;
    }

    private Scout addNewScoutFromPreScout(NewScoutFormDto form) {
        PreScout preScout = this.preScoutService.findById(form.preScoutId());

        if (preScout.isHasBeenInGroup() && !form.hasBeenBefore()) {
            throw new WebBentayaBadRequestException("Error al mandar la preinscripción, contacte con informática");
        }

        Scout scout = preScout.isHasBeenInGroup() && form.existingScoutId() != null ?
            reactivateExistingScout(form) :
            createNewScout(form);

        preScoutService.saveAsAssigned(form.preScoutId());

        return scout;
    }

    private Scout reactivateExistingScout(NewScoutFormDto form) {
        Scout scout = scoutService.findById(form.existingScoutId());
        scoutPersonalDataService.updatePersonalData(PersonalDataFormDto.fromNewScoutForm(form), scout.getPersonalData());
        scout.getContactList().clear();
        scout.getContactList().add(scoutContactService.newContact(form.contact(), scout));
        scout.getEconomicData().setBank(form.bank());
        scout.getEconomicData().setBank(form.iban());
        this.setExistingGroupData(scout, form);
        return scout;
    }

    private Scout createNewScout(NewScoutFormDto form) {
        Scout scout = new Scout();

        this.setNewGroupData(scout, form);

        PersonalData personalData = new PersonalData();
        personalData.setScout(scout);
        scout.setPersonalData(personalData);
        scoutPersonalDataService.updatePersonalData(PersonalDataFormDto.fromNewScoutForm(form), personalData);

        if (scout.getScoutType() == ScoutType.SCOUT) {
            scout.setContactList(List.of(scoutContactService.newContact(form.contact(), scout)));
        }

        EconomicData economicData = new EconomicData();
        economicData.setScout(scout);
        scout.setEconomicData(economicData);
        if (scout.getScoutType() == ScoutType.SCOUT) {
            economicData.setIban(form.iban());
            economicData.setBank(form.bank());
        }

        ScoutHistory scoutHistory = new ScoutHistory();
        scoutHistory.setScout(scout);
        scout.setScoutHistory(scoutHistory);

        MedicalData medicalData = new MedicalData();
        medicalData.setScout(scout);
        scout.setMedicalData(medicalData);
        medicalData.setBloodType(BloodType.NA);

        return scout;
    }

    private void validate(NewScoutFormDto form) {
        if (form.scoutType() == ScoutType.SCOUT) {
            if (form.contact() == null) {
                throw new WebBentayaBadRequestException("Debe especificar un contacto a la persona educanda");
            }
            boolean contactIsDonor = form.contact().donor();
            if (contactIsDonor && form.contact().idDocument() == null) {
                throw new WebBentayaBadRequestException("Debe especificar el documento de identidad del contacto por su condición de donante");
            } else if (!contactIsDonor && form.idDocument() == null) {
                throw new WebBentayaBadRequestException("Debe especificar el documento de identidad de la persona educanda por su condición de donante");
            }
        } else if (form.scoutType() != ScoutType.INACTIVE) {
            if (form.email() == null) {
                throw new WebBentayaBadRequestException("Debe especificar el correo de la scout");
            }
            if (form.phone() == null) {
                throw new WebBentayaBadRequestException("Debe especificar el teléfono móvil de la scout");
            }
        }
    }

    private void setNewGroupData(Scout scout, NewScoutFormDto form) {
        this.setGroupData(scout, form, ScoutStatus.PENDING_NEW);
        scout.setRegistrationDates(List.of(
            new ScoutRegistrationDates().setRegistrationDate(form.firstActivityDate()).setScout(scout)
        ));
    }

    private void setExistingGroupData(Scout scout, NewScoutFormDto form) {
        this.setGroupData(scout, form, ScoutStatus.PENDING_EXISTING);
        List<ScoutRegistrationDates> registrationDates = scout.getRegistrationDates();

        ScoutRegistrationDates scoutRegistrationDates = new ScoutRegistrationDates()
            .setRegistrationDate(form.firstActivityDate())
            .setScout(scout);

        registrationDates.add(scoutRegistrationDates);
    }

    private void setGroupData(Scout scout, NewScoutFormDto form, ScoutStatus scoutStatus) {
        scoutGroupDataService.updateScoutCensus(scout, form.census());

        scout.setScoutType(form.scoutType());
        if (scout.getScoutType() == ScoutType.INACTIVE) {
            scout.setStatus(ScoutStatus.INACTIVE);
            scout.setFederated(false);
        } else if (scout.getCensus() == null) {
            scout.setStatus(scoutStatus);
            scout.setFederated(false);
        } else {
            scout.setStatus(ScoutStatus.ACTIVE);
            scout.setFederated(scout.getScoutType().isScoutOrScouter());
        }

        scout.setGroup(scout.getScoutType().isScoutOrScouter() && form.groupId() != 0 ?
            groupRepository.findById(form.groupId()).orElseThrow(WebBentayaNotFoundException::new) :
            null
        );
    }
}
