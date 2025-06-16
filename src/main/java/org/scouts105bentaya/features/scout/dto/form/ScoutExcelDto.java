package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.constraints.NotEmpty;
import org.scouts105bentaya.features.scout.specification.ScoutSpecificationFilter;

import java.util.List;

public record ScoutExcelDto(
    @NotEmpty List<ScoutExcelField> fields,
    ScoutSpecificationFilter filter
) {
}
