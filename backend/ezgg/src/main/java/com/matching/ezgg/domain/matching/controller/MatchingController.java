package com.matching.ezgg.domain.matching.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.global.annotation.LoginUser;
import com.matching.ezgg.domain.matching.dto.PreferredPartnerParsingDto;
import com.matching.ezgg.domain.matching.service.MatchingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;

	@PostMapping("/matching/start")
	public void startMatching(@RequestBody PreferredPartnerParsingDto preferredPartnerDto, @LoginUser Long memberId) {
		matchingService.startMatching(memberId, preferredPartnerDto);
	}
}
