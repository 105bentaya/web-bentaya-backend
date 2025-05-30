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
import org.scouts105bentaya.features.special_member.specification.SpecialMemberSpecificationFilter;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.specification.PageDto;
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
@PreAuthorize("hasRole('ADMIN')")
public class SpecialMemberController {

    private final SpecialMemberService specialMemberService;

    public SpecialMemberController(SpecialMemberService specialMemberService) {
        this.specialMemberService = specialMemberService;
    }

    @GetMapping
    public PageDto<SpecialMemberBasicDataDto> getSpecialMembers(SpecialMemberSpecificationFilter filter) {
        return GenericConverter.convertListToPageDto(specialMemberService.findAll(filter), SpecialMemberBasicDataDto::fromEntity);
    }

    @GetMapping("/{id}")
    public SpecialMemberDetailDto getSpecialMember(@PathVariable Integer id) {
        return specialMemberService.findDetailsById(id);
    }

    @GetMapping("/last-census/{role}")
    public int getSpecialMemberLastCensus(@PathVariable SpecialMemberRole role) {
        return specialMemberService.findLastCensus(role);
    }

    @GetMapping("/search-scout")
    public List<FilteredResultDto> getScoutFiltered(@RequestParam String filter) {
        return specialMemberService.searchScout(filter);
    }

    @GetMapping("/search-special-member")
    public List<FilteredResultDto> getSpecialMemberFiltered(@RequestParam String filter) {
        return specialMemberService.searchSpecialMember(filter);
    }

    @PostMapping
    public SpecialMemberBasicDataDto saveSpecialMember(@RequestBody @Valid SpecialMemberFormDto specialMemberFormDto) {
        return SpecialMemberBasicDataDto.fromEntity(specialMemberService.saveSpecialMember(specialMemberFormDto));
    }

    @PutMapping("/{id}")
    public SpecialMemberDetailDto updateSpecialMember(@PathVariable Integer id, @RequestBody @Valid SpecialMemberFormDto specialMemberFormDto) {
        return specialMemberService.updatedSpecialMember(id, specialMemberFormDto);
    }

    @PostMapping("/donation/{memberId}")
    public SpecialMemberDonation addDonation(
        @PathVariable Integer memberId,
        @RequestBody @Valid SpecialMemberDonationFormDto form
    ) {
        return specialMemberService.addDonation(memberId, form);
    }

    @PutMapping("/donation/{memberId}/{donationId}")
    public SpecialMemberDonation updateDonation(
        @PathVariable Integer donationId,
        @PathVariable Integer memberId,
        @RequestBody @Valid SpecialMemberDonationFormDto form
    ) {
        return specialMemberService.updateDonation(memberId, donationId, form);
    }

    @DeleteMapping("/donation/{memberId}/{donationId}")
    public void deleteDonation(@PathVariable Integer donationId, @PathVariable Integer memberId) {
        specialMemberService.deleteDonation(memberId, donationId);
    }
}
