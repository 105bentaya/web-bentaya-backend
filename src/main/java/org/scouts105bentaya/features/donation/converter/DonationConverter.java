package org.scouts105bentaya.features.donation.converter;

import org.scouts105bentaya.features.donation.Donation;
import org.scouts105bentaya.features.donation.dto.DonationDto;
import org.scouts105bentaya.features.payment.Payment;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DonationConverter extends GenericConverter<Donation, DonationDto> {
    @Override
    public Donation convertFromDto(DonationDto dto) {
        throw new UnsupportedOperationException(GenericConstants.NOT_IMPLEMENTED);
    }

    @Override
    public DonationDto convertFromEntity(Donation entity) {
        return new DonationDto(
            entity.getId(),
            entity.getName(),
            entity.getFirstSurname(),
            entity.getSecondSurname(),
            entity.getCif(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getDeduct(),
            entity.getAmount(),
            entity.getFrequency(),
            entity.getSingleDonationPaymentType(),
            entity.getIban(),
            Optional.ofNullable(entity.getPayment()).orElse(new Payment()).getStatus(),
            entity.getCreationDate()
        );
    }
}
