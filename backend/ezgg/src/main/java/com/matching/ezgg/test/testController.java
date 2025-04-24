package com.matching.ezgg.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.global.exception.TestException;
import com.matching.ezgg.matching.dto.PreferredPartnerDto;
import com.matching.ezgg.matching.service.MatchingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class testController {
	private final MatchingService matchingService;

	@GetMapping("/test")
	public void test() {
		throw new TestException();
	}

	@GetMapping("/testMatching")
	public void testMatching(@RequestBody PreferredPartnerDto preferredPartnerDto) {
		matchingService.startMatching(1L, preferredPartnerDto);//TODO CustomUserDetailService에서 실제 memberId 받아오기
	}
}
