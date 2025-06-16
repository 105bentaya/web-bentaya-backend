package org.scouts105bentaya.features.scout.dto.form;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.List;

public record ScoutExcelField(
    @NotNull String field,
    @NotNull String label,
    @Nullable String pipe,
    @Nullable List<ScoutExcelField> listFields
) {
    public String[] getFields() {
        return field.split("\\.");
    }
    public String[] getFieldsFromList() {
        String[] fields = getFields();
        return Arrays.copyOfRange(fields, 1, fields.length);
    }
}
