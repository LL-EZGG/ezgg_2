package com.matching.ezgg.domain.matching.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.api.service.MatchService;
import com.matching.ezgg.domain.memberInfo.entity.MemberInfo;
import com.matching.ezgg.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.domain.recentTwentyMatch.entity.RecentTwentyMatch;
import com.matching.ezgg.domain.recentTwentyMatch.service.RecentTwentyMatchBuilderService;
import com.matching.ezgg.domain.recentTwentyMatch.service.RecentTwentyMatchService;
import com.matching.ezgg.api.dto.MatchDto;
import com.matching.ezgg.domain.recentTwentyMatch.dto.RecentTwentyMatchDto;
import com.matching.ezgg.api.dto.WinRateNTierDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchingDataBulkSaveService {
	private final MemberInfoService memberInfoService;
	private final MatchService matchService;
	private final RecentTwentyMatchService recentTwentyMatchService;
	private final RecentTwentyMatchBuilderService recentTwentyMatchBuilderService;

	@Transactional
	public MemberInfo saveAllAggregatedData(
		Long memberId,
		WinRateNTierDto winRateNTierDto,
		List<String> fetchedMatchIds,
		List<MatchDto> matchDtoList,
		boolean existsNewMatchIds
	) {
		// memberInfo 업데이트
		MemberInfo memberInfo = memberInfoService.updateMemberInfo(memberId, winRateNTierDto, fetchedMatchIds, existsNewMatchIds);

		// matchInfo 업데이트
		// 어떤 matchInfo 저장 중 에러가 나왔는지 판단하기 위해 saveAll 적용X
		for (MatchDto matchDto : matchDtoList) {
			matchService.save(matchDto);
		}

		return memberInfo;
	}

	// recentTwentyMatch 계산 후 저장
	public RecentTwentyMatch calculateAndSaveRecentTwentyMatch(boolean existsNewMatchIds, String puuid, Long memberId){

		// recentTwentyMatch 계산
		RecentTwentyMatchDto recentTwentyMatchDto = new RecentTwentyMatchDto();
		if (existsNewMatchIds) recentTwentyMatchDto = recentTwentyMatchBuilderService.buildDto(puuid);

		// recentTwentyMatch 업데이트
		RecentTwentyMatch recentTwentyMatch;
		if (existsNewMatchIds) {
			recentTwentyMatch = recentTwentyMatchService.upsertRecentTwentyMatch(recentTwentyMatchDto);
		} else {
			recentTwentyMatch = recentTwentyMatchService.getRecentTwentyMatchByMemberId(memberId);
		}
		return recentTwentyMatch;
	}




}
