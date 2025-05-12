package com.matching.ezgg.domain.memberInfo.service;

import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matching.dto.MemberDataBundle;
import com.matching.ezgg.domain.member.dto.MemberInfoDto;
import com.matching.ezgg.domain.recentTwentyMatch.dto.RecentTwentyMatchDto;
import com.matching.ezgg.domain.recentTwentyMatch.service.RecentTwentyMatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDataBundleService {

	private final MemberInfoService memberInfoService;
	private final RecentTwentyMatchService recentTwentyMatchService;

	public MemberDataBundle getMemberDataBundleByMemberId(Long memberId) {
		log.info("memberId : {}", memberId);

		// MemberInfoDto 및 RecentTwentyMatchDto로 변환
		return MemberDataBundle.builder()
			.memberInfoDto(MemberInfoDto.toDto(memberInfoService.getMemberInfoByMemberId(memberId)))
			.recentTwentyMatchDto(
				RecentTwentyMatchDto.toDto(recentTwentyMatchService.getRecentTwentyMatchByMemberId(memberId)))
			.build();
	}
}
