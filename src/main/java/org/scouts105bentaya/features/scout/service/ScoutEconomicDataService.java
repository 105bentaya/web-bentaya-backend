package org.scouts105bentaya.features.scout.service;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.dto.form.EconomicDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.EconomicEntryFormDto;
import org.scouts105bentaya.features.scout.entity.EconomicData;
import org.scouts105bentaya.features.scout.entity.EconomicEntry;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.repository.EconomicEntryRepository;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class ScoutEconomicDataService {

    private final ScoutRepository scoutRepository;
    private final EconomicEntryRepository economicEntryRepository;

    public ScoutEconomicDataService(ScoutRepository scoutRepository, EconomicEntryRepository economicEntryRepository) {
        this.scoutRepository = scoutRepository;
        this.economicEntryRepository = economicEntryRepository;
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
            (form.income() == null && form.spending() == null) ||
            (form.income() != null && form.spending() != null)
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
            .setDate(form.date())
            .setDescription(form.description())
            .setAmount(form.amount())
            .setAccount(form.account())
            .setType(form.type())
            .setObservations(form.observations());

        if (form.income() != null) {
            entry.setIncome(form.income());
            entry.setSpending(null);
        } else {
            entry.setIncome(null);
            entry.setSpending(form.spending());
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
