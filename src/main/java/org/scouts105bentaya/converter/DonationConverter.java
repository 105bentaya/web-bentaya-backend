package org.scouts105bentaya.converter;

import org.scouts105bentaya.dto.DonationDto;
import org.scouts105bentaya.entity.Donation;
import org.scouts105bentaya.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DonationConverter extends GenericConverter<Donation, DonationDto> {
    @Override
    public Donation convertFromDto(DonationDto dto) {
        throw new UnsupportedOperationException("Method not supported");
    }

    @Override
    public DonationDto convertFromEntity(Donation entity) {
        DonationDto dto = new DonationDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setFirstSurname(entity.getFirstSurname());
        dto.setSecondSurname(entity.getSecondSurname());
        dto.setCif(entity.getCif());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setDeduct(entity.getDeduct());
        dto.setAmount(entity.getAmount());
        dto.setFrequency(entity.getFrequency());
        dto.setSingleDonationPaymentType(entity.getSingleDonationPaymentType());
        dto.setStatus(Optional.ofNullable(entity.getPayment()).orElse(new Payment()).getStatus());
        dto.setIban(entity.getIban());
        dto.setCreationDate(entity.getCreationDate());
        return dto;
    }
}
