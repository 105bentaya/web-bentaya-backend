package org.scouts105bentaya.features.donation.converter;

import org.scouts105bentaya.features.donation.Donation;
import org.scouts105bentaya.features.donation.dto.DonationFormDto;
import org.scouts105bentaya.shared.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class DonationFormConverter extends GenericConverter<Donation, DonationFormDto> {

    @Override
    public Donation convertFromDto(DonationFormDto dto) {
        Donation entity = new Donation();
        entity.setName(dto.name());
        entity.setFirstSurname(dto.firstSurname());
        entity.setSecondSurname(dto.secondSurname());
        entity.setCif(dto.cif());
        entity.setPhone(dto.phone());
        entity.setEmail(dto.email());
        entity.setDeduct(dto.deduct());
        entity.setAmount(dto.amount());
        entity.setFrequency(dto.frequency());
        entity.setSingleDonationPaymentType(dto.singleDonationPaymentType());
        entity.setIban(dto.iban());
        return entity;
    }

    @Override
    public DonationFormDto convertFromEntity(Donation entity) {
        throw new UnsupportedOperationException("Method not supported");
    }
}
