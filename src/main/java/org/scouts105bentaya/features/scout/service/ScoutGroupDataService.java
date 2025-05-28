package org.scouts105bentaya.features.scout.service;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.util.IntervalUtils;
import org.scouts105bentaya.features.group.GroupRepository;
import org.scouts105bentaya.features.scout.repository.ScoutFileRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRecordRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.scout.dto.form.ScoutInfoFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutRecordFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutRegistrationDateFormDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.entity.ScoutRecord;
import org.scouts105bentaya.features.scout.entity.ScoutRegistrationDates;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScoutGroupDataService {

    private final ScoutRepository scoutRepository;
    private final BlobService blobService;
    private final ScoutFileRepository scoutFileRepository;
    private final GroupRepository groupRepository;
    private final ScoutService scoutService;
    private final ScoutRecordRepository scoutRecordRepository;
    private final ScoutFileService scoutFileService;

    public ScoutGroupDataService(
        ScoutRepository scoutRepository,
        BlobService blobService,
        ScoutFileRepository scoutFileRepository,
        GroupRepository groupRepository,
        ScoutService scoutService,
        ScoutRecordRepository scoutRecordRepository,
        ScoutFileService scoutFileService
    ) {
        this.scoutRepository = scoutRepository;
        this.blobService = blobService;
        this.scoutFileRepository = scoutFileRepository;
        this.groupRepository = groupRepository;
        this.scoutService = scoutService;
        this.scoutRecordRepository = scoutRecordRepository;
        this.scoutFileService = scoutFileService;
    }

    public Scout updateScoutInfo(Integer id, ScoutInfoFormDto form) {
        this.validateScoutGroup(form);
        this.validateCensus(form, id);
        this.validateRegistrationDates(form);

        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        scout.setCensus(form.census());
        scout.setScoutType(form.scoutType());

        if (scout.getScoutType() == ScoutType.INACTIVE) {
            scout.setActive(false);
            scout.setFederated(false);
        } else {
            scout.setActive(true);
            scout.setFederated(form.federated());
        }

        scout.setGroup(scout.getScoutType().hasGroup() && form.groupId() != 0 ?
            groupRepository.findById(form.groupId()).orElseThrow(WebBentayaNotFoundException::new) :
            null
        );

        this.updateScoutRegistrationDates(scout, form);

        //todo hacer actualizaciones de roles, usuarios y asistencias según cosas
        //        if (hasChangedGroup) {
        //            this.deleteFutureConfirmations(savedScout);
        //            this.createConfirmationForFutureEvents(scoutDB);
        //        }


        return scoutRepository.save(scout);
    }

    private void updateScoutRegistrationDates(Scout scout, ScoutInfoFormDto form) {
        List<ScoutRegistrationDates> registrationDates = scout.getRegistrationDates();
        List<ScoutRegistrationDates> newDates = new ArrayList<>();

        form.registrationDates().forEach(newDate -> {
            if (newDate.id() != null) {
                ScoutRegistrationDates existingDate = registrationDates.stream()
                    .filter(date -> date.getId().equals(newDate.id()))
                    .findFirst().orElseThrow(WebBentayaNotFoundException::new);
                existingDate.setRegistrationDate(newDate.registrationDate());
                existingDate.setUnregistrationDate(newDate.unregistrationDate());
            } else {
                newDates.add(new ScoutRegistrationDates()
                    .setRegistrationDate(newDate.registrationDate())
                    .setUnregistrationDate(newDate.unregistrationDate())
                    .setScout(scout)
                );
            }
        });

        registrationDates.removeIf(date -> form.registrationDates().stream()
            .noneMatch(newDate -> date.getId().equals(newDate.id()))
        );
        registrationDates.addAll(newDates);
    }

    private void validateCensus(ScoutInfoFormDto form, Integer scoutId) {
        Optional<Scout> existingCensus = scoutRepository.findFirstByCensus(form.census());
        if (existingCensus.isPresent() && !existingCensus.get().getId().equals(scoutId)) {
            throw new WebBentayaConflictException("Este censo ya está asignado");
        }
    }

    private void validateScoutGroup(ScoutInfoFormDto form) {
        if (form.scoutType() == ScoutType.SCOUT && (form.groupId() == null || form.groupId() == 0)) {
            throw new WebBentayaBadRequestException("Es necesario especificar la unidad de la educanda");
        }
        if (form.scoutType() == ScoutType.SCOUTER && form.groupId() == null) {
            throw new WebBentayaBadRequestException("Es necesario especificar la unidad de la educadora");
        }
    }

    private void validateRegistrationDates(ScoutInfoFormDto form) {
        List<ScoutRegistrationDateFormDto> dates = form.registrationDates();

        if (dates.stream()
            .filter(date -> date.unregistrationDate() != null)
            .anyMatch(date -> !date.registrationDate().isBefore(date.unregistrationDate()))
        ) {
            throw new WebBentayaBadRequestException("Una fecha de baja debe ser posterior a la fecha de alta correspondiente");
        }

        if (dates.stream().filter(date -> date.unregistrationDate() == null).count() > 1) {
            throw new WebBentayaBadRequestException("Hay dos o más fechas de baja sin especificar");
        }

        List<Interval> intervals = dates.stream()
            .map(date -> IntervalUtils.intervalFromLocalDates(
                date.registrationDate(), Optional.ofNullable(date.unregistrationDate()).orElse(date.registrationDate())
            ))
            .toList();

        if (IntervalUtils.intervalsOverlapOrAbut(intervals)) {
            throw new WebBentayaBadRequestException("Hay fechas de alta y baja que se superponen");
        }
    }

    public ScoutRecord uploadScoutRecord(Integer scoutId, ScoutRecordFormDto recordForm) {
        Scout scout = scoutService.findById(scoutId);

        ScoutRecord scoutRecord = new ScoutRecord()
            .setRecordType(recordForm.recordType())
            .setStartDate(recordForm.startDate())
            .setEndDate(recordForm.endDate())
            .setObservations(recordForm.observations())
            .setScout(scout);

        scoutRecord = scoutRecordRepository.save(scoutRecord);

        scout.getRecordList().add(scoutRecord);
        scoutRepository.save(scout);

        return scoutRecord;
    }

    public ScoutRecord updateScoutRecord(Integer scoutId, Integer recordId, ScoutRecordFormDto recordForm) {
        Scout scout = scoutService.findById(scoutId);

        ScoutRecord scoutRecord = scout.getRecordList().stream()
            .filter(r -> r.getId().equals(recordId))
            .findFirst()
            .orElseThrow(WebBentayaNotFoundException::new);

        scoutRecord.setRecordType(recordForm.recordType())
            .setStartDate(recordForm.startDate())
            .setEndDate(recordForm.endDate())
            .setObservations(recordForm.observations());

        return scoutRecordRepository.save(scoutRecord);
    }

    @Synchronized
    public ScoutFile uploadRecordFile(Integer recordId, MultipartFile file) {
        FileUtils.validateFileIsPdf(file); //todo check
        ScoutRecord scoutRecord = scoutRecordRepository.findById(recordId).orElseThrow(WebBentayaNotFoundException::new);

        ScoutFile scoutFile = scoutFileService.createScoutFile(file);

        scoutFile = scoutFileRepository.save(scoutFile);
        scoutRecord.getFiles().add(scoutFile);
        scoutRecordRepository.save(scoutRecord);
        return scoutFile;
    }

    public void deleteRecordFile(Integer recordId, Integer fileId) {
        ScoutRecord scoutRecord = scoutRecordRepository.findById(recordId).orElseThrow(WebBentayaNotFoundException::new);
        List<ScoutFile> recordFiles = scoutRecord.getFiles();

        ScoutFile scoutFile = recordFiles.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        blobService.deleteBlob(scoutFile.getUuid());

        recordFiles.remove(scoutFile);
        scoutRecordRepository.save(scoutRecord);

        scoutFileRepository.deleteById(fileId);
    }

    public void deleteScoutRecord(Integer scoutId, Integer recordId) {
        Scout scout = scoutService.findById(scoutId);
        ScoutRecord scoutRecord = scout.getRecordList().stream()
            .filter(r -> r.getId().equals(recordId))
            .findFirst()
            .orElseThrow(WebBentayaNotFoundException::new);

        scoutRecord.getFiles().forEach(file -> blobService.deleteBlob(file.getUuid()));
        scoutRecordRepository.deleteById(recordId);
    }
}
