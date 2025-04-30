package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record InsuranceHolderForm(
        Integer contactId,
        @Length(max = 255) String name,
        @Length(max = 255) String surname,
        @Valid IdDocumentFormDto idDocument,
        @Length(max = 255) String phone,
        @Length(max = 255) @Email String email
    ) {
    }
