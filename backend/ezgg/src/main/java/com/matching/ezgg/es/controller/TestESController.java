package com.matching.ezgg.es.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.es.index.MatchingUserES;
import com.matching.ezgg.es.repository.MatchingUserRepository;
import com.matching.ezgg.global.response.SuccessResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestESController {

	private final MatchingUserRepository matchingUserRepository;

	@PostMapping("/es/test")
	public ResponseEntity<SuccessResponse<Void>> esTest(@RequestBody MatchingUserES matchingUserES) {
		matchingUserRepository.save(matchingUserES);
		return ResponseEntity.ok(SuccessResponse.<Void>builder()
			.code("200")
			.message("success")
			.build());
	}

}
