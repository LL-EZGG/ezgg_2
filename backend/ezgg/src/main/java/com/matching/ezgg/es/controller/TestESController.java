package com.matching.ezgg.es.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.es.index.MatchingUserES;
import com.matching.ezgg.es.repository.MatchingUserRepository;
import com.matching.ezgg.es.service.EsMatchingFilter;
import com.matching.ezgg.global.response.SuccessResponse;
import com.matching.ezgg.matching.dto.MatchingFilterDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestESController {

	private final MatchingUserRepository matchingUserRepository;
	private final EsMatchingFilter esMatchingFilter;

	@PostMapping("/es/test")
	public ResponseEntity<SuccessResponse<Void>> esTest(@RequestBody MatchingUserES matchingUserES) {
		matchingUserRepository.save(matchingUserES);
		return ResponseEntity.ok(SuccessResponse.<Void>builder()
			.code("200")
			.message("success")
			.build());
	}

	// 테스트용
	@GetMapping("es/matching/{my-line}/{partner-line}/{tier}/{member-id}/{preferredChampion}/{unpreferredChampion}")
	public ResponseEntity<List<MatchingFilterDto>> testMatching(@PathVariable("my-line") String myLine,
		@PathVariable("partner-line") String partnerLine,
		@PathVariable("tier") String tier,
		@PathVariable("member-id") Long memberId,
		@PathVariable("preferredChampion") String preferredChampion,
		@PathVariable("unpreferredChampion") String unpreferredChampion) {
		return ResponseEntity.ok().body(esMatchingFilter.findMatchingUsers(myLine, partnerLine, tier, memberId,
			preferredChampion, unpreferredChampion
		));
	}

}
