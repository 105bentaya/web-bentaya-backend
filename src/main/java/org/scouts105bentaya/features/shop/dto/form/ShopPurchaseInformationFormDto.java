package org.scouts105bentaya.features.shop.dto.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.scouts105bentaya.shared.GenericConstants;

public record ShopPurchaseInformationFormDto(
    @NotNull @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String name,
    @NotNull @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String surname,
    @NotNull @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String phone,
    @NotNull @Email @Length(max = GenericConstants.MYSQL_BASIC_VARCHAR_LENGTH) String email,
    @Length(max = GenericConstants.MYSQL_TEXT_LENGTH) String observations
) {
}
