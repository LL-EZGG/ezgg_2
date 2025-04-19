package com.matching.ezgg.matching.service;

import org.springframework.stereotype.Service;

import com.matching.ezgg.api.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.api.service.ApiService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchingService {
	private final MemberInfoService memberInfoService;
	private final ApiService apiService;

	public void startMatching(Long memberId) {
		String puuid = memberInfoService.getMemberPuuidByMemberId(memberId);
		updateAllAttributesOfMember(puuid);
		//TODO createEsMatchingDocument(), StartMatchingByDocuments(), ...
	}

	public void updateAllAttributesOfMember(String puuid){
		memberInfoService.updateWinRateNTier(apiService.getMemberWinRateNTier(puuid));

		//TODO match, recentTwentyMatch 업데이트

	}

	private void createEsMatchingDocument(){
		//TODO
	}

}
