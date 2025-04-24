package com.matching.ezgg.matching.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.matching.ezgg.api.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.api.dto.MatchDto;
import com.matching.ezgg.api.dto.RecentTwentyMatchDto;
import com.matching.ezgg.api.domain.recentTwentyMatch.service.RecentTwentyMatchBuilderService;
import com.matching.ezgg.api.dto.WinRateNTierDto;
import com.matching.ezgg.api.service.ApiService;
import com.matching.ezgg.matching.dto.MemberDataBundle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {
	private final MemberInfoService memberInfoService;
	private final ApiService apiService;
	private final RecentTwentyMatchBuilderService recentTwentyMatchBuilderService;
	private final MatchingDataBulkSaveService matchingDataBulkSaveService;

	// 매칭 시작 시 호출
	public void startMatching(Long memberId) {
		log.info("매칭 시작! memberId = {}", memberId);
		MemberDataBundle MemInfoNRecentTwentyMatch = updateAllAttributesOfMember(memberId);
		//TODO createEsMatchingDocument(), StartMatchingByDocuments(), ...
	}

	// 매칭 시작 전 모든 데이터 업데이트
	public MemberDataBundle updateAllAttributesOfMember(Long memberId){

		String puuid = memberInfoService.getMemberPuuidByMemberId(memberId);
		log.info("Riot Api로 모든 데이터 저장 시작: {}", puuid);

		// 티어+승률/matchIds api 요청해서 메모리에 저장
		WinRateNTierDto memberWinRateNTier = apiService.getMemberWinRateNTier(puuid);
		List<String> fetchedMatchIds = apiService.getMemberMatchIds(puuid);
		List<String> newlyAddedMatchIds = getNewMatchIds(puuid, fetchedMatchIds);
		boolean existNewMatchIds = !newlyAddedMatchIds.isEmpty();

		// matchInfo api 요청해서 메모리에 저장
		List<MatchDto> matchDtoList = new ArrayList<>();
		for (String matchId : newlyAddedMatchIds) {
			MatchDto matchInfo = apiService.getMemberMatch(puuid, matchId);
			matchDtoList.add(matchInfo);
		}

		// recentTwentyMatch 계산
		RecentTwentyMatchDto recentTwentyMatchDto = new RecentTwentyMatchDto();
		if (existNewMatchIds) recentTwentyMatchDto = recentTwentyMatchBuilderService.buildDto(puuid);


		// api로 받아온 데이터 한 트랜잭션으로 저장
		// return 객체 = {memberInfo : MemberInfo, recentTwentyMatch : RecentTwentyMatch}
		MemberDataBundle MemInfoNRecentTwentyMatch = matchingDataBulkSaveService.saveAllAggregatedData(
			memberId, memberWinRateNTier, fetchedMatchIds, matchDtoList, recentTwentyMatchDto, existNewMatchIds
		);




		log.info("Riot Api로 모든 데이터 저장 종료: {}", puuid);
		return MemInfoNRecentTwentyMatch;
	}

	private void createEsMatchingDocument(){
		//TODO
	}

	// 새로운 matchId가 없으면 null 리스트 리턴. 있으면 matchIds 업데이트 후 새로운 matchId 리스트 리턴
	public List<String> getNewMatchIds(String puuid, List<String> fetchedMatchIds) {
		return memberInfoService.extractNewMatchIds(puuid, fetchedMatchIds);
	}
}
