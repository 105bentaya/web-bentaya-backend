package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.PartnershipDto;


public interface PartnershipService {
    void sendPartnershipEmail(PartnershipDto partnershipDto);
}
