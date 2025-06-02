package org.scouts105bentaya.features.scout.service;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.scout.dto.form.ScoutHistoryFormDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutHistory;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScoutHistoryService {

    private final ScoutRepository scoutRepository;

    public ScoutHistoryService(ScoutRepository scoutRepository) {
        this.scoutRepository = scoutRepository;
    }

    public Scout updateScoutHistory(Integer id, ScoutHistoryFormDto form) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        ScoutHistory scoutHistory = scout.getScoutHistory();
        scoutHistory.setObservations(form.observations());
        scoutHistory.setProgressions(form.progressions());

        return scoutRepository.save(scout);
    }
}
