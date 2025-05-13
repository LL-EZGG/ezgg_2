package com.matching.ezgg.matching.service;

import org.springframework.stereotype.Service;

import com.matching.ezgg.dataProcessor.memberInfo.service.MemberInfoService;
import com.matching.ezgg.dataProcessor.recentTwentyMatch.dto.RecentTwentyMatchDto;
import com.matching.ezgg.dataProcessor.recentTwentyMatch.service.RecentTwentyMatchService;
import com.matching.ezgg.matching.dto.MemberDataBundleDto;
import com.matching.ezgg.dataProcessor.memberInfo.dto.MemberInfoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDataBundleService {

	private final MemberInfoService memberInfoService;
	private final RecentTwentyMatchService recentTwentyMatchService;

	public MemberDataBundleDto getMemberDataBundleByMemberId(Long memberId) {
		log.info("memberId : {}", memberId);

		// memberId로 MemberInfo 및  RecentTwentyMatch 조회
		MemberInfoDto memberInfoDto = memberInfoService.findByMemberId(memberId);
		RecentTwentyMatchDto recentTwentyMatchDto = RecentTwentyMatchDto.toDto(recentTwentyMatchService.getRecentTwentyMatchByMemberId(memberId));

		return new MemberDataBundleDto(memberInfoDto, recentTwentyMatchDto);
	}
}
