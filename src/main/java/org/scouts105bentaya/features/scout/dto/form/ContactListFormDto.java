package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;

import java.util.List;

public record ContactListFormDto(
    @Valid List<ContactFormDto> contactList
) {
}
