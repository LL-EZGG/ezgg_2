package com.matching.ezgg.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.api.dto.MatchDto;
import com.matching.ezgg.api.dto.WinRateNTierDto;
import com.matching.ezgg.api.service.ApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/riotapi")
@RequiredArgsConstructor
public class ApiController {

	private final ApiService apiService;

	//테스트용-티어
	@GetMapping("tier/{puuid}")//TODO 테스트 컨트롤러. 배포시 삭제
	public ResponseEntity<WinRateNTierDto> getWinRateNTier(@PathVariable("puuid") String puuid) {
		WinRateNTierDto dto = apiService.getMemberWinRateNTier(puuid);
		return ResponseEntity.ok().body(dto);
	}

	//테스트용-matchIds
	@GetMapping("matchIds/{puuid}")//TODO 테스트 컨트롤러. 배포시 삭제
	public ResponseEntity<List<String>> getMatchIds(@PathVariable("puuid") String puuid) {
		List<String> matchIds = apiService.getMemberMatchIds(puuid);
		return ResponseEntity.ok().body(matchIds);
	}

}
