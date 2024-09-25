package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.PreScouterDto;
import org.scouts105bentaya.entity.PreScouter;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PreScouterService {
    List<PreScouter> findAll();
    void saveAndSendEmail(PreScouterDto preScouterDto);
    ResponseEntity<byte[]> getPDF(Integer id);
    void delete(Integer id);
}
