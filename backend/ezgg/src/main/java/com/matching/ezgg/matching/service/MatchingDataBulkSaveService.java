package com.matching.ezgg.matching.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.api.domain.match.service.MatchService;
import com.matching.ezgg.api.domain.memberInfo.entity.MemberInfo;
import com.matching.ezgg.api.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.api.domain.recentTwentyMatch.entity.RecentTwentyMatch;
import com.matching.ezgg.api.domain.recentTwentyMatch.service.RecentTwentyMatchService;
import com.matching.ezgg.api.dto.MatchDto;
import com.matching.ezgg.api.dto.RecentTwentyMatchDto;
import com.matching.ezgg.api.dto.WinRateNTierDto;
import com.matching.ezgg.matching.dto.MemberDataBundle;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchingDataBulkSaveService {
	private final MemberInfoService memberInfoService;
	private final MatchService matchService;
	private final RecentTwentyMatchService recentTwentyMatchService;

	@Transactional
	public MemberDataBundle saveAllAggregatedData(
		Long memberId,
		WinRateNTierDto winRateNTierDto,
		List<String> fetchedMatchIds,
		List<MatchDto> matchDtoList,
		RecentTwentyMatchDto recentTwentyMatchDto,
		boolean existsNewMatchIds
	) {
		// memberInfo 업데이트
		MemberInfo memberInfo = memberInfoService.updateMemberInfo(memberId, winRateNTierDto, fetchedMatchIds, existsNewMatchIds);

		// matchInfo 업데이트
		// 어떤 matchInfo 저장 중 에러가 나왔는지 판단하기 위해 saveAll 적용X
		for (MatchDto matchDto : matchDtoList) {
			matchService.save(matchDto);
		}

		// recentTwentyMatch 업데이트
		RecentTwentyMatch recentTwentyMatch;
		if (existsNewMatchIds) {
			recentTwentyMatch = recentTwentyMatchService.upsertRecentTwentyMatch(recentTwentyMatchDto);
		} else {
			recentTwentyMatch = recentTwentyMatchService.getRecentTwentyMatchByMemberId(memberId);
		}

		return MemberDataBundle.builder()
			.memberInfo(memberInfo)
			.recentTwentyMatch(recentTwentyMatch)
			.build();
	}

}
