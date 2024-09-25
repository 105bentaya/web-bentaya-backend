package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.DonationFormDto;
import org.scouts105bentaya.entity.Donation;
import org.springframework.stereotype.Component;

@Component
public class DonationFormConverter extends GenericConverter<Donation, DonationFormDto> {

    @Override
    public Donation convertFromDto(DonationFormDto dto) {
        Donation entity = new Donation();
        entity.setName(dto.getName());
        entity.setFirstSurname(dto.getFirstSurname());
        entity.setSecondSurname(dto.getSecondSurname());
        entity.setCif(dto.getCif());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setDeduct(dto.getDeduct());
        entity.setAmount(dto.getAmount());
        entity.setFrequency(dto.getFrequency());
        entity.setSingleDonationPaymentType(dto.getSingleDonationPaymentType());
        entity.setIban(dto.getIban());
        return entity;
    }

    @Override
    public DonationFormDto convertFromEntity(Donation entity) {
        throw new UnsupportedOperationException("Method not supported");
    }
}
