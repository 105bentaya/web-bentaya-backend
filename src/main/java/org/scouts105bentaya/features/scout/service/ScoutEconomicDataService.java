package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.invoice.repository.InvoiceExpenseTypeRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceIncomeTypeRepository;
import org.scouts105bentaya.features.scout.ScoutUtils;
import org.scouts105bentaya.features.scout.dto.ScoutDonorDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicEntryDonorFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicEntryFormDto;
import org.scouts105bentaya.features.scout.entity.Contact;
import org.scouts105bentaya.features.scout.entity.EconomicData;
import org.scouts105bentaya.features.scout.entity.EconomicEntry;
import org.scouts105bentaya.features.scout.entity.EconomicEntryDonor;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.EntryType;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.scouts105bentaya.features.scout.repository.EconomicEntryRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.specification.DonationEntrySpecification;
import org.scouts105bentaya.features.scout.specification.DonationEntrySpecificationFilter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ScoutEconomicDataService {

    private final ScoutRepository scoutRepository;
    private final EconomicEntryRepository economicEntryRepository;
    private final InvoiceExpenseTypeRepository invoiceExpenseTypeRepository;
    private final InvoiceIncomeTypeRepository invoiceIncomeTypeRepository;

    public ScoutEconomicDataService(
        ScoutRepository scoutRepository,
        EconomicEntryRepository economicEntryRepository,
        InvoiceExpenseTypeRepository invoiceExpenseTypeRepository,
        InvoiceIncomeTypeRepository invoiceIncomeTypeRepository
    ) {
        this.scoutRepository = scoutRepository;
        this.economicEntryRepository = economicEntryRepository;
        this.invoiceExpenseTypeRepository = invoiceExpenseTypeRepository;
        this.invoiceIncomeTypeRepository = invoiceIncomeTypeRepository;
    }

    public Page<EconomicEntry> findAllDonations(DonationEntrySpecificationFilter filter) {
        return economicEntryRepository.findAll(new DonationEntrySpecification(filter), filter.getPageable());
    }

    public ScoutDonorDto findDonorByScoutId(Integer scoutId) {
        Scout scout = scoutRepository.get(scoutId);
        return this.getScoutDonor(scout);
    }

    public ScoutDonorDto getScoutDonor(Scout scout) {
        Optional<Contact> first = scout.getContactList().stream().filter(Contact::isDonor).findFirst();
        if (first.isPresent()) {
            Contact contact = first.get();
            if (contact.getPersonType() == PersonType.REAL) {
                return new ScoutDonorDto(
                    contact.getName(),
                    contact.getSurname(),
                    contact.getIdDocument(),
                    contact.getPersonType()
                );
            }
            return new ScoutDonorDto(
                contact.getCompanyName(),
                null,
                contact.getIdDocument(),
                contact.getPersonType()
            );
        }
        PersonalData personalData = scout.getPersonalData();
        return new ScoutDonorDto(
            personalData.getName(),
            personalData.getSurname(),
            personalData.getIdDocument(),
            PersonType.REAL
        );
    }

    public Scout updateEconomicData(Integer id, EconomicDataFormDto form) {
        Scout scout = scoutRepository.get(id);
        EconomicData data = scout.getEconomicData();

        scout.getContactList().forEach(contact -> contact.setDonor(false));
        if (form.donorId() != null) {
            Contact newDonor = scout.getContactList().stream()
                .filter(contact -> contact.getId().equals(form.donorId()))
                .findFirst().orElseThrow(WebBentayaNotFoundException::new);
            if (newDonor.getIdDocument() == null) {
                throw new WebBentayaBadRequestException("El contacto donante debe tener un documento de identidad asociado");
            }
            newDonor.setDonor(true);
        } else if (scout.getPersonalData().getIdDocument() == null) {
            throw new WebBentayaBadRequestException("La asociada debe tener un documento de identidad asociado para ser considerada donante");
        }

        data.setIban(form.iban());
        data.setBank(form.bank());

        return scoutRepository.save(scout);
    }


    public EconomicEntry addEntry(Integer scoutId, EconomicEntryFormDto form) {
        this.validateEntryForm(form);

        Scout scout = scoutRepository.get(scoutId);
        EconomicEntry entry = new EconomicEntry();
        this.updateEntryFromForm(entry, form);
        entry.setEconomicData(scout.getEconomicData());

        return economicEntryRepository.save(entry);
    }

    private void validateEntryForm(EconomicEntryFormDto form) {
        if (
            form.expenseId() == null && form.incomeId() == null ||
            form.expenseId() != null && form.incomeId() != null
        ) {
            throw new WebBentayaBadRequestException("Debe especificar un único gasto o ingreso");
        }

        if (form.type() == EntryType.DONATION && form.donor() == null) {
            throw new WebBentayaBadRequestException("Debe especificar el donante de la donación");
        }
    }

    public EconomicEntry updateEntry(Integer scoutId, Integer entryId, EconomicEntryFormDto form) {
        this.validateEntryForm(form);

        Scout scout = scoutRepository.get(scoutId);

        EconomicEntry entry = scout.getEconomicData().getEntries().stream()
            .filter(e -> e.getId().equals(entryId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        this.updateEntryFromForm(entry, form);

        return economicEntryRepository.save(entry);
    }

    private void updateEntryFromForm(EconomicEntry entry, EconomicEntryFormDto form) {
        entry
            .setIssueDate(form.issueDate())
            .setDueDate(form.dueDate())
            .setDescription(form.description())
            .setAmount(form.amount())
            .setAccount(form.account())
            .setType(form.type())
            .setObservations(form.observations());

        if (form.incomeId() != null) {
            entry.setIncomeType(invoiceIncomeTypeRepository.findById(form.incomeId()).orElseThrow(WebBentayaNotFoundException::new));
            entry.setExpenseType(null);
        } else {
            entry.setIncomeType(null);
            entry.setExpenseType(invoiceExpenseTypeRepository.findById(form.expenseId()).orElseThrow(WebBentayaNotFoundException::new));
        }

        if (form.type() == EntryType.DONATION) {
            updateEntryDonor(entry, form.donor());
        } else {
            entry.setDonor(null);
        }
    }

    private void updateEntryDonor(EconomicEntry entry, EconomicEntryDonorFormDto form) {
        EconomicEntryDonor donor = entry.getDonor() == null ? new EconomicEntryDonor() : entry.getDonor();

        donor
            .setName(form.name())
            .setSurname(form.surname())
            .setIdDocument(ScoutUtils.updateIdDocument(donor.getIdDocument(), form.idDocument()))
            .setPersonType(form.personType())
            .setEconomicEntry(entry);

        entry.setDonor(donor);
    }

    public void deleteEntry(Integer scoutId, Integer entryId) {
        scoutRepository.get(scoutId).getEconomicData().getEntries().stream()
            .filter(e -> e.getId().equals(entryId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        economicEntryRepository.deleteById(entryId);
    }

}
