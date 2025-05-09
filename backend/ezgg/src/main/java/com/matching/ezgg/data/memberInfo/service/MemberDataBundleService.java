package com.matching.ezgg.data.memberInfo.service;

import org.springframework.stereotype.Service;

import com.matching.ezgg.data.memberInfo.repository.MemberInfoRepository;
import com.matching.ezgg.matching.dto.MemberDataBundle;
import com.matching.ezgg.data.memberInfo.entity.MemberInfo;
import com.matching.ezgg.data.recentTwentyMatch.entity.RecentTwentyMatch;
import com.matching.ezgg.data.recentTwentyMatch.repository.RecentTwentyMatchRepository;
import com.matching.ezgg.global.exception.MatchNotFoundException;
import com.matching.ezgg.global.exception.MemberInfoNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDataBundleService {

	private final MemberInfoRepository memberRepository;
	private final RecentTwentyMatchRepository recentTwentyMatchRepository;

	public MemberDataBundle getMemberDataBundleByMemberId(Long memberId) {
		log.info("memberId : {}", memberId);

		// memberId로 MemberInfo 및  RecentTwentyMatch 조회
		MemberInfo memberInfo = memberRepository.findById(memberId).orElseThrow(
			MemberInfoNotFoundException::new);
		RecentTwentyMatch recentTwentyMatch = recentTwentyMatchRepository.findByMemberId(memberId).orElseThrow(
			MatchNotFoundException::new);

		return new MemberDataBundle(memberInfo, recentTwentyMatch);
	}
}
