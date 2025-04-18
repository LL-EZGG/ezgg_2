package com.matching.ezgg.api.controller;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.api.dto.WinRateNTierDto;
import com.matching.ezgg.api.service.ApiService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/riotapi")
@RequiredArgsConstructor
public class ApiController {

	private final ApiService apiService;

	//puuid 조회
	@GetMapping("/{riot-id}/{tag}")
	public Mono<ResponseEntity<String>> postRiotIdTag(@PathVariable("riot-id") String riotId,
		@PathVariable("tag") String tag) {
		return apiService.getMemberPuuid(riotId, tag)
			.map(ResponseEntity::ok);
	}

	//테스트용-티어
	@GetMapping("tier/{puuid}")
	public Mono<ResponseEntity<WinRateNTierDto>> getWinRateNTier(@PathVariable("puuid") String puuid) {
		return apiService.getMemberWinRateNTier(Mono.just(puuid))
			.map(ResponseEntity::ok);
	}

	//테스트용-matchIds
	@GetMapping("match/{puuid}")
	public Mono<ResponseEntity<ArrayList<String>>> getMatchIds(@PathVariable("puuid") String puuid) {
		return apiService.getMatchIds(Mono.just(puuid))
			.map(ResponseEntity::ok);

	}
}
