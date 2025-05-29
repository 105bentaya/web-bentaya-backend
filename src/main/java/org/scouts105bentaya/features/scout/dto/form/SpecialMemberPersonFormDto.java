package org.scouts105bentaya.features.scout.dto.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.scouts105bentaya.shared.GenericConstants;

public record SpecialMemberPersonFormDto(
    Integer id,
    @NotNull PersonType type,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String name,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String surname,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String companyName,
    @NotNull @Valid IdDocumentFormDto idDocument,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String phone,
    @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) @Email String email
    ) {
}
