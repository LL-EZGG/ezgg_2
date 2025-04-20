package com.matching.ezgg.matching.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.matching.ezgg.api.domain.match.service.MatchService;
import com.matching.ezgg.api.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.api.service.ApiService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchingService {
	private final MemberInfoService memberInfoService;
	private final MatchService matchService;
	private final ApiService apiService;

	public void startMatching(Long memberId) {
		String puuid = memberInfoService.getMemberPuuidByMemberId(memberId);
		updateAllAttributesOfMember(puuid);
		//TODO createEsMatchingDocument(), StartMatchingByDocuments(), ...
	}

	public void updateAllAttributesOfMember(String puuid){
		// 티어, 승률 업데이트
		memberInfoService.updateWinRateNTier(apiService.getMemberWinRateNTier(puuid));

		// matchIds 업데이트 후 새롭게 추가된 matchId 리스트 리턴
		List<String> newlyAddedMatchIds = updateAndGetNewMatchIds(puuid, apiService.getMemberMatchIds(puuid));

		// 새로운 match 들 저장
		for (String matchId : newlyAddedMatchIds) {
			matchService.save(apiService.getMemberMatch(puuid, matchId));
		}

		//TODO recentTwentyMatch 업데이트

	}

	private void createEsMatchingDocument(){
		//TODO
	}

	// 새롭게 추가된 matchId가 없으면 null리스트 리턴. 있으면 matchIds 업데이트 후 새롭게 저장된 matchId 리스트 리턴
	public List<String> updateAndGetNewMatchIds(String puuid, List<String> fetchedMatchIds) {
		List<String> newlyAddedMatchIds = memberInfoService.extractNewMatchIds(puuid, fetchedMatchIds);

		if (newlyAddedMatchIds != null && !newlyAddedMatchIds.isEmpty()) {
			memberInfoService.updateMatchIds(puuid, fetchedMatchIds);
		}

		return newlyAddedMatchIds;
	}

}
