package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.ScoutDto;
import org.scouts105bentaya.dto.ScoutFormUserUpdateDto;
import org.scouts105bentaya.entity.Scout;

import java.util.List;
import java.util.Set;

public interface ScoutService {
    List<Scout> findAll();
    List<Scout> adminFindAll();
    List<Scout> findAllWithFalseImageAuthorization();
    List<Scout> findAllByLoggedScouterGroupId();
    List<String> findScoutUsernames(Integer id);
    Set<Scout> findCurrentByUser();
    Scout findById(Integer id);
    Scout save(ScoutDto scout);
    Scout saveFromPreScoutAndDelete(ScoutDto scoutDto, Integer preScoutId);
    Scout update(ScoutDto scout);
    void updateScoutUsers(Integer scoutId, List<String> usernames);
    ScoutFormUserUpdateDto getScoutFormUpdateUserMessage(Integer scoutId, List<String> newUsers);
    void disable(Integer id);
    void delete(Integer id);
}
