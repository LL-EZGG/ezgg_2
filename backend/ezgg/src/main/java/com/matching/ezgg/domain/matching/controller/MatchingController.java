package com.matching.ezgg.domain.matching.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.matching.ezgg.domain.matching.dto.PreferredPartnerParsingDto;
import com.matching.ezgg.domain.matching.service.MatchingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;

	@MessageMapping("/matching/start")
	public void startMatching(@Payload PreferredPartnerParsingDto preferredPartnerDto, Principal principal) {
		Long memberId = Long.valueOf(principal.getName());
		matchingService.startMatching(memberId, preferredPartnerDto);
	}
}
