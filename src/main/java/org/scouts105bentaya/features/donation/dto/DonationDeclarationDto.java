package org.scouts105bentaya.features.donation.dto;

public interface DonationDeclarationDto {
    int getIsInKind();
    String getIdNumber();
    int getAmount();
    int getIsJuridical();
    String getName();
    String getSurname();

    default boolean isInKind() {
        return getIsInKind() == 1;
    }

    default boolean isJuridical() {
        return getIsJuridical() == 1;
    }
}