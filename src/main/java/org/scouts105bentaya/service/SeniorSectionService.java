package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.SeniorFormDto;
import org.scouts105bentaya.entity.SeniorForm;

import java.util.List;

public interface SeniorSectionService {
    List<SeniorForm> getAll();
    void saveSeniorForm(SeniorFormDto formDto);
    void delete(Integer id);
}
