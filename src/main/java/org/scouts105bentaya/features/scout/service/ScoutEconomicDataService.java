package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.invoice.repository.InvoiceExpenseTypeRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceIncomeTypeRepository;
import org.scouts105bentaya.features.scout.dto.form.EconomicDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicEntryFormDto;
import org.scouts105bentaya.features.scout.entity.EconomicData;
import org.scouts105bentaya.features.scout.entity.EconomicEntry;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.repository.EconomicEntryRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.springframework.stereotype.Service;

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

    public Scout updateEconomicData(Integer id, EconomicDataFormDto form) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        EconomicData data = scout.getEconomicData();

        scout.getContactList().forEach(contact -> contact.setDonor(false));
        if (form.donorId() != null) {
            scout.getContactList().stream()
                .filter(contact -> contact.getId().equals(form.donorId()))
                .findFirst().orElseThrow(WebBentayaNotFoundException::new)
                .setDonor(true);
        }

        data.setIban(form.iban());
        data.setBank(form.bank());

        return scoutRepository.save(scout);
    }


    public EconomicEntry addEntry(Integer scoutId, EconomicEntryFormDto form) {
        this.validateEntryForm(form);

        Scout scout = scoutRepository.findById(scoutId).orElseThrow(WebBentayaNotFoundException::new);
        EconomicEntry entry = new EconomicEntry();
        this.updateEntryFromForm(entry, form);
        entry.setEconomicData(scout.getEconomicData());

        return economicEntryRepository.save(entry);
    }

    private void validateEntryForm(EconomicEntryFormDto form) {
        if (
            form.expenseId() == null && form.incomeId() == null ||
            form.expenseId() != null && form.incomeId() == null
        ) {
            throw new WebBentayaBadRequestException("Debe especificar un único gasto o ingreso");
        }
    }

    public EconomicEntry updateEntry(Integer scoutId, Integer entryId, EconomicEntryFormDto form) {
        this.validateEntryForm(form);

        Scout scout = scoutRepository.findById(scoutId).orElseThrow(WebBentayaNotFoundException::new);

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
    }

    public void deleteEntry(Integer scoutId, Integer entryId) {
        scoutRepository.findById(scoutId).orElseThrow(WebBentayaNotFoundException::new)
            .getEconomicData().getEntries().stream()
            .filter(e -> e.getId().equals(entryId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        economicEntryRepository.deleteById(entryId);
    }

}
