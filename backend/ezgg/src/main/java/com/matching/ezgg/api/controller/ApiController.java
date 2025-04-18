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

@RestController
@RequestMapping("/riotapi")
@RequiredArgsConstructor
public class ApiController {

	private final ApiService apiService;

	// //puuid 조회
	// @GetMapping("/{riot-id}/{tag}")
	// public ResponseEntity<String> postRiotIdTag(@PathVariable("riot-id") String riotId,
	// 	@PathVariable("tag") String tag) {
	// 	String puuid = apiService.getMemberPuuid(riotId, tag);
	// 	return ResponseEntity.ok().body(puuid);
	// }//TODO 굳이 백엔드 서버내에서 api를 주고받을 이유가 없다.

	//테스트용-티어
	@GetMapping("tier/{puuid}")//TODO 테스트 컨트롤러. 배포시 삭제
	public ResponseEntity<WinRateNTierDto> getWinRateNTier(@PathVariable("puuid") String puuid) {
		WinRateNTierDto dto = apiService.getMemberWinRateNTier(puuid);
		return ResponseEntity.ok().body(dto);
	}

	//테스트용-matchIds
	@GetMapping("match/{puuid}")//TODO 테스트 컨트롤러. 배포시 삭제
	public ResponseEntity<ArrayList<String>> getMatchIds(@PathVariable("puuid") String puuid) {
		ArrayList<String> matchIds = apiService.getMatchIds(puuid);
		return ResponseEntity.ok().body(matchIds);
	}
}
