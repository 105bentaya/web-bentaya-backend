package org.scouts105bentaya.features.booking;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.booking.converter.ScoutCenterConverter;
import org.scouts105bentaya.features.booking.dto.ScoutCenterDto;
import org.scouts105bentaya.features.booking.repository.ScoutCenterRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/scout-center")
public class ScoutCenterController {

    private final ScoutCenterConverter scoutCenterConverter;
    private final ScoutCenterRepository scoutCenterRepository;

    public ScoutCenterController(ScoutCenterConverter scoutCenterConverter, ScoutCenterRepository scoutCenterRepository) {
        this.scoutCenterConverter = scoutCenterConverter;
        this.scoutCenterRepository = scoutCenterRepository;
    }

    @GetMapping("/public")
    public List<ScoutCenterDto> getAllScoutCenters() {
        return scoutCenterConverter.convertEntityCollectionToDtoList(scoutCenterRepository.findAll());
    }
}
