package com.matching.ezgg.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.global.exception.TestException;
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
	public void testMatching() {
		matchingService.startMatching(1L);
	}
}
