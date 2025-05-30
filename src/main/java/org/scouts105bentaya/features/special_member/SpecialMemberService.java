package org.scouts105bentaya.features.special_member;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.FilteredResultDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.service.ScoutService;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.special_member.dto.SpecialMemberDetailDto;
import org.scouts105bentaya.features.special_member.dto.form.SpecialMemberDonationFormDto;
import org.scouts105bentaya.features.special_member.dto.form.SpecialMemberFormDto;
import org.scouts105bentaya.features.special_member.dto.form.SpecialMemberPersonFormDto;
import org.scouts105bentaya.features.special_member.entity.SpecialMember;
import org.scouts105bentaya.features.special_member.entity.SpecialMemberDonation;
import org.scouts105bentaya.features.special_member.entity.SpecialMemberPerson;
import org.scouts105bentaya.features.special_member.repository.SpecialMemberPersonRepository;
import org.scouts105bentaya.features.special_member.repository.SpecialMemberRepository;
import org.scouts105bentaya.features.special_member.specification.SpecialMemberSpecification;
import org.scouts105bentaya.features.special_member.specification.SpecialMemberSpecificationFilter;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SpecialMemberService {

    private final ScoutService scoutService;
    private final SpecialMemberRepository specialMemberRepository;
    private final SettingService settingService;
    private final ScoutRepository scoutRepository;
    private final SpecialMemberPersonRepository specialMemberPersonRepository;
    private final SpecialMemberDonationRepository specialMemberDonationRepository;

    public SpecialMemberService(
        ScoutService scoutService,
        SpecialMemberRepository specialMemberRepository,
        SettingService settingService, ScoutRepository scoutRepository,
        SpecialMemberPersonRepository specialMemberPersonRepository,
        SpecialMemberDonationRepository specialMemberDonationRepository
    ) {
        this.scoutService = scoutService;
        this.specialMemberRepository = specialMemberRepository;
        this.settingService = settingService;
        this.scoutRepository = scoutRepository;
        this.specialMemberPersonRepository = specialMemberPersonRepository;
        this.specialMemberDonationRepository = specialMemberDonationRepository;
    }

    public Page<SpecialMember> findAll(SpecialMemberSpecificationFilter filter) {
        log.info("findAll - {}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberRepository.findAll(new SpecialMemberSpecification(filter), filter.getPageable());
    }

    private SpecialMember findById(Integer id) {
        return this.specialMemberRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public SpecialMemberDetailDto findDetailsById(Integer id) {
        return specialMemberDetails(this.findById(id));
    }

    private SpecialMemberDetailDto specialMemberDetails(SpecialMember specialMember) {
        List<SpecialMember> records = new ArrayList<>();
        if (Optional.ofNullable(specialMember.getScout()).isPresent()) {
            records = specialMember.getScout().getSpecialRoles();
        } else if (Optional.ofNullable(specialMember.getPerson()).isPresent()) {
            records = specialMember.getPerson().getSpecialMembers();
        }

        return SpecialMemberDetailDto.fromEntities(specialMember, records);
    }

    public int findLastCensus(SpecialMemberRole role) {
        return Integer.parseInt(settingService.findByName(SettingEnum.getSpecialMemberSetting(role)).getValue());
    }

    @Transactional
    public SpecialMember saveSpecialMember(SpecialMemberFormDto form) {
        this.validateSaveForm(form);

        SpecialMember specialMember = new SpecialMember();

        if (form.scoutId() != null) {
            specialMember.setScout(scoutService.findById(form.scoutId()));
        } else if (form.personId() != null) {
            specialMember.setPerson(specialMemberPersonRepository.findById(form.personId()).orElseThrow(WebBentayaNotFoundException::new));
        } else {
            specialMember.setPerson(personFromForm(form.person(), new SpecialMemberPerson()));
        }

        specialMember
            .setRole(form.role())
            .setAgreementDate(form.agreementDate())
            .setAwardDate(form.awardDate())
            .setDetails(form.details())
            .setObservations(form.observations())
            .setRoleCensus(form.roleCensus());

        this.settingService.updateValue(form.roleCensus(), SettingEnum.getSpecialMemberSetting(form.role()));

        return specialMemberRepository.save(specialMember);
    }

    private void validateSaveForm(SpecialMemberFormDto form) {
        if (specialMemberRepository.findByRoleAndRoleCensus(form.role(), form.roleCensus()).isPresent()) {
            throw new WebBentayaConflictException("Ya existe un registro con el censo indicado");
        }
        if (form.person() == null && form.scoutId() == null && form.personId() == null) {
            throw new WebBentayaBadRequestException("Un registro debe estar asociado a una persona o a un scout");
        }
        if (form.person() != null && form.scoutId() != null && form.personId() != null) {
            throw new WebBentayaBadRequestException("Un registro sólo puede estar asociado a una persona o a un scout, no a ambos");
        }
        if (form.scoutId() != null && specialMemberRepository.findByRoleAndScoutId(form.role(), form.scoutId()).isPresent()) {
            throw new WebBentayaConflictException("La persona asociada ya tiene un registro de este tipo");
        }
        if (form.personId() != null && specialMemberRepository.findByRoleAndPersonId(form.role(), form.personId()).isPresent()) {
            throw new WebBentayaConflictException("La persona indicada ya tiene un registro de este tipo");
        }
    }

    @Transactional
    public SpecialMemberDetailDto updatedSpecialMember(Integer specialMemberId, SpecialMemberFormDto form) {
        SpecialMember specialMember = this.specialMemberRepository.findById(specialMemberId).orElseThrow(WebBentayaNotFoundException::new);

        this.validateUpdateForm(form, specialMember);

        if (specialMember.getPerson() != null) {
            specialMember.setPerson(personFromForm(form.person(), specialMember.getPerson()));
        }

        specialMember
            .setAgreementDate(form.agreementDate())
            .setAwardDate(form.awardDate())
            .setDetails(form.details())
            .setObservations(form.observations())
            .setRoleCensus(form.roleCensus());

        if (specialMember.getRoleCensus() > findLastCensus(specialMember.getRole())) {
            this.settingService.updateValue(specialMember.getRoleCensus(), SettingEnum.getSpecialMemberSetting(form.role()));
        }

        return specialMemberDetails(this.specialMemberRepository.save(specialMember));
    }

    private void validateUpdateForm(SpecialMemberFormDto form, SpecialMember existingSpecialMember) {
        if (form.role() != existingSpecialMember.getRole()) {
            throw new WebBentayaBadRequestException("No se puede cambiar el tipo de registro");
        }

        Optional<SpecialMember> sameCensusMember = specialMemberRepository.findByRoleAndRoleCensus(form.role(), form.roleCensus());
        if (sameCensusMember.isPresent() && !sameCensusMember.get().getId().equals(existingSpecialMember.getId())) {
            throw new WebBentayaConflictException("Ya existe un registro con el censo indicado");
        }
        if (existingSpecialMember.getPerson() != null && form.person() == null) {
            throw new WebBentayaBadRequestException("No se puede eliminar el persona asociada al registro");
        }
        if (existingSpecialMember.getScout() != null && form.person() != null && form.personId() != null) {
            throw new WebBentayaBadRequestException("No se puede actualizar la persona asociada al registro");
        }
    }


    private SpecialMemberPerson personFromForm(SpecialMemberPersonFormDto form, SpecialMemberPerson person) {
        person.setType(form.type())
            .setEmail(form.email())
            .setPhone(form.phone())
            .setIdDocument(ScoutUtils.updateIdDocument(person.getIdDocument(), form.idDocument()));

        if (person.getType() == PersonType.REAL) {
            person.setName(form.name())
                .setSurname(form.surname())
                .setCompanyName(null);
        } else {
            person.setName(null)
                .setSurname(null)
                .setCompanyName(form.companyName());
        }

        return person;
    }

    public List<FilteredResultDto> searchScout(String filter) {
        return scoutRepository.findByBasicFields("%%%s%%".formatted(filter)).stream().map(scout -> new FilteredResultDto(
            scout.getId(), generateScoutLabel(scout)
        )).toList();
    }

    private String generateScoutLabel(Scout scout) {
        String result = scout.getPersonalData().getName() + " " + scout.getPersonalData().getSurname();
        if (scout.getCensus() != null) {
            result += " - " + scout.getCensus();
        }

        if (scout.getPersonalData().getIdDocument() != null) {
            result += " - " + scout.getPersonalData().getIdDocument().getNumber();
        }

        return result;
    }

    public List<FilteredResultDto> searchSpecialMember(String filter) {
        return specialMemberPersonRepository.findByBasicFields("%%%s%%".formatted(filter)).stream().map(person -> new FilteredResultDto(
            person.getId(), generatePersonLabel(person)
        )).toList();
    }

    private String generatePersonLabel(SpecialMemberPerson person) {
        String result = "";

        if (person.getType() == PersonType.REAL) {
            result += person.getName() + " " + person.getSurname();
        } else {
            result += person.getCompanyName();
        }

        if (person.getIdDocument() != null) {
            result += " - " + person.getIdDocument().getNumber();
        }

        return result;
    }

    public SpecialMemberDonation addDonation(Integer memberId, SpecialMemberDonationFormDto form) {
        this.validateDonationForm(form);

        SpecialMember specialMember = this.findById(memberId);
        SpecialMemberDonation donation = new SpecialMemberDonation();
        this.updateDonationFromForm(donation, form);
        donation.setSpecialMember(specialMember);

        return specialMemberDonationRepository.save(donation);
    }

    private void validateDonationForm(SpecialMemberDonationFormDto form) {
        if (form.type() == DonationType.ECONOMIC && (form.amount() == null || form.bankAccount() == null || form.paymentType() == null)) {
            throw new WebBentayaBadRequestException("Debe especificar la cantidad, forma de pago y cuenta bancaria en una donación económica");
        } else if (form.type() == DonationType.IN_KIND && form.inKindDonationType() == null) {
            throw new WebBentayaBadRequestException("Debe especificar el tipo de especies");
        }
    }

    public SpecialMemberDonation updateDonation(Integer memberId, Integer donationId, SpecialMemberDonationFormDto form) {
        SpecialMember specialMember = this.findById(memberId);
        this.validateDonationForm(form);

        SpecialMemberDonation donation = specialMember.getDonations().stream()
            .filter(d -> d.getId().equals(donationId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        this.updateDonationFromForm(donation, form);

        return specialMemberDonationRepository.save(donation);
    }

    private void updateDonationFromForm(SpecialMemberDonation donation, SpecialMemberDonationFormDto form) {
        donation
            .setDate(form.date())
            .setType(form.type())
            .setNotes(form.notes());

        if (donation.getType() == DonationType.ECONOMIC) {
            donation.setAmount(form.amount())
                .setPaymentType(form.paymentType())
                .setBankAccount(form.bankAccount())
                .setInKindDonationType(null);
        } else {
            donation.setAmount(null)
                .setPaymentType(null)
                .setBankAccount(null)
                .setInKindDonationType(form.inKindDonationType());
        }
    }

    public void deleteDonation(Integer memberId, Integer donationId) {
        this.findById(memberId)
            .getDonations().stream()
            .filter(d -> d.getId().equals(donationId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        specialMemberDonationRepository.deleteById(donationId);
    }

}
