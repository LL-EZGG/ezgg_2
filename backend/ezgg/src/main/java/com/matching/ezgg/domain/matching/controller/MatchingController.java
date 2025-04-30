package com.matching.ezgg.domain.matching.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.matching.ezgg.domain.matching.dto.PreferredPartnerParsingDto;
import com.matching.ezgg.domain.matching.service.MatchingService;
import com.matching.ezgg.global.annotation.LoginUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;

	@MessageMapping("/matching/start")
	public void startMatching(@RequestBody PreferredPartnerParsingDto preferredPartnerDto, Principal principal) {
		Long memberId = Long.valueOf(principal.getName());
		System.out.println("\n\n[WebSocket]\n\nmemberId: " + memberId);
		matchingService.startMatching(memberId, preferredPartnerDto);
	}
}
