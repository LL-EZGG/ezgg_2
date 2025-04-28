package com.matching.ezgg.matching.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.matching.dto.PreferredPartnerParsingDto;
import com.matching.ezgg.matching.redis.RedisStreamProducer;
import com.matching.ezgg.matching.service.MatchingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;
	private final RedisStreamProducer redisStreamProducer;

	@PostMapping("/matching/start/{memberId}")
	public void startMatching(@RequestBody PreferredPartnerParsingDto preferredPartnerDto, @PathVariable(name = "memberId") Long memberId) {
		matchingService.startMatching(memberId, preferredPartnerDto);
	}

}
