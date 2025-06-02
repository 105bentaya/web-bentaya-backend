package org.scouts105bentaya.features.scout.dto.form;

import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.shared.GenericConstants;

public record ScoutHistoryFormDto(
    @Length(max = GenericConstants.MYSQL_TEXT_LENGTH) String observations,
    @Length(max = GenericConstants.MYSQL_TEXT_LENGTH) String progressions
) {
}
