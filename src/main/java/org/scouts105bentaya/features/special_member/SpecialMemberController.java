package org.scouts105bentaya.features.special_member;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.scout.dto.FilteredResultDto;
import org.scouts105bentaya.features.special_member.dto.SpecialMemberBasicDataDto;
import org.scouts105bentaya.features.special_member.dto.SpecialMemberDetailDto;
import org.scouts105bentaya.features.special_member.dto.form.SpecialMemberDonationFormDto;
import org.scouts105bentaya.features.special_member.dto.form.SpecialMemberFormDto;
import org.scouts105bentaya.features.special_member.entity.SpecialMemberDonation;
import org.scouts105bentaya.features.special_member.enums.SpecialMemberRole;
import org.scouts105bentaya.features.special_member.repository.SpecialMemberDonationRepository;
import org.scouts105bentaya.features.special_member.specification.SpecialMemberSpecificationFilter;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/special-member")
public class SpecialMemberController {

    private final SpecialMemberService specialMemberService;
    private final SpecialMemberDonationRepository specialMemberDonationRepository;

    public SpecialMemberController(
        SpecialMemberService specialMemberService,
        SpecialMemberDonationRepository specialMemberDonationRepository
    ) {
        this.specialMemberService = specialMemberService;
        this.specialMemberDonationRepository = specialMemberDonationRepository;
    }

    @PreAuthorize("hasRole('ROLE_SECRETARY') or hasRole('TRANSACTION') and @authLogic.filterIsDonor(#filter)")
    @GetMapping()
    public PageDto<SpecialMemberBasicDataDto> getSpecialMembers(SpecialMemberSpecificationFilter filter) {
        log.info("getSpecialMembers - filter:{}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        return GenericConverter.convertListToPageDto(specialMemberService.findAll(filter), SpecialMemberBasicDataDto::fromEntity);
    }

    @PreAuthorize("hasRole('ROLE_SECRETARY') or hasRole('TRANSACTION') and @authLogic.specialMemberIsDonor(#id)")
    @GetMapping("/{id}")
    public SpecialMemberDetailDto getSpecialMember(@PathVariable Integer id) {
        log.info("getSpecialMember - id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberService.findDetailsById(id);
    }

    @PreAuthorize("hasAnyRole('TRANSACTION')")
    @GetMapping("/donations")
    public List<SpecialMemberDonation> getDonations() {
        log.info("getDonations{}", SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberDonationRepository.findAll();
    }

    @PreAuthorize("hasRole('ROLE_SECRETARY') or hasRole('TRANSACTION') and @authLogic.roleIsDonor(#role)")
    @GetMapping("/last-census/{role}")
    public int getSpecialMemberLastCensus(@PathVariable SpecialMemberRole role) {
        log.info("getSpecialMemberLastCensus - role:{}{}", role, SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberService.findLastCensus(role);
    }

    @PreAuthorize("hasAnyRole('TRANSACTION', 'SECRETARY')")
    @GetMapping("/search-scout")
    public List<FilteredResultDto> getScoutFiltered(@RequestParam String filter) {
        log.info("getScoutFiltered - filter:{}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberService.searchScout(filter);
    }

    @PreAuthorize("hasAnyRole('TRANSACTION', 'SECRETARY')")
    @GetMapping("/search-special-member")
    public List<FilteredResultDto> getSpecialMemberFiltered(@RequestParam String filter) {
        log.info("getSpecialMemberFiltered - filter:{}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberService.searchSpecialMember(filter);
    }

    @PreAuthorize("hasRole('ROLE_SECRETARY') or hasRole('TRANSACTION') and @authLogic.roleIsDonor(#specialMemberFormDto.role)")
    @PostMapping
    public SpecialMemberBasicDataDto saveSpecialMember(@RequestBody @Valid SpecialMemberFormDto specialMemberFormDto) {
        log.info("saveSpecialMember{}", SecurityUtils.getLoggedUserUsernameForLog());
        return SpecialMemberBasicDataDto.fromEntity(specialMemberService.saveSpecialMember(specialMemberFormDto));
    }

    @PreAuthorize("hasRole('ROLE_SECRETARY') or hasRole('TRANSACTION') and @authLogic.canUpdateDonor(#id, #specialMemberFormDto.role)")
    @PutMapping("/{id}")
    public SpecialMemberDetailDto updateSpecialMember(@PathVariable Integer id, @RequestBody @Valid SpecialMemberFormDto specialMemberFormDto) {
        log.info("updateSpecialMember - id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberService.updatedSpecialMember(id, specialMemberFormDto);
    }

    @PreAuthorize("hasAnyRole('TRANSACTION', 'SECRETARY')")
    @PostMapping("/donation/{memberId}")
    public SpecialMemberDonation addDonation(
        @PathVariable Integer memberId,
        @RequestBody @Valid SpecialMemberDonationFormDto form
    ) {
        log.info("addDonation - memberId:{}{}", memberId, SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberService.addDonation(memberId, form);
    }

    @PreAuthorize("hasAnyRole('TRANSACTION', 'SECRETARY')")
    @PutMapping("/donation/{donationId}")
    public SpecialMemberDonation updateDonation(
        @PathVariable Integer donationId,
        @RequestBody @Valid SpecialMemberDonationFormDto form
    ) {
        log.info("updateDonation - donationId:{}{}", donationId, SecurityUtils.getLoggedUserUsernameForLog());
        return specialMemberService.updateDonation(donationId, form);
    }

    @PreAuthorize("hasAnyRole('TRANSACTION', 'SECRETARY')")
    @DeleteMapping("/donation/{donationId}")
    public void deleteDonation(@PathVariable Integer donationId) {
        log.info("deleteDonation - donationId:{}{}", donationId, SecurityUtils.getLoggedUserUsernameForLog());
        specialMemberService.deleteDonation(donationId);
    }
}
