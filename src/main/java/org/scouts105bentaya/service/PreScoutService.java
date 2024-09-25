package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.PreScoutAssignationDto;
import org.scouts105bentaya.dto.PreScoutDto;
import org.scouts105bentaya.entity.PreScout;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PreScoutService {
    List<PreScout> findAll();

    PreScout findById(int id);

    List<PreScout> findAllAssignedByLoggedScouter();

    void saveAssignation(PreScoutAssignationDto dto);
    void updateAssignation(PreScoutAssignationDto dto);

    void saveAndSendEmail(PreScoutDto preScoutDto);

    ResponseEntity<byte[]> getPDF(Integer id);

    void delete(Integer id);
}
