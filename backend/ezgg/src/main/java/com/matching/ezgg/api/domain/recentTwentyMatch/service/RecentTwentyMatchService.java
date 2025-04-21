package com.matching.ezgg.api.domain.recentTwentyMatch.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.api.dto.RecentTwentyMatchDto;
import com.matching.ezgg.api.domain.recentTwentyMatch.entity.RecentTwentyMatch;
import com.matching.ezgg.api.domain.recentTwentyMatch.repository.RecentTwentyMatchRepository;
import com.matching.ezgg.global.exception.RecentTwentyMatchNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecentTwentyMatchService {

	private final RecentTwentyMatchRepository recentTwentyMatchRepository;

	// memberId를 가지고 있는 recentTwentyMatch 존재 여부 조회
	public boolean existsByMemberId(Long memberId) {
		return recentTwentyMatchRepository.existsByMemberId(memberId);
	}

	// memberId로 recentTwentyMatch 조회
	public RecentTwentyMatch getRecentTwentyMatchByMemberId(Long memberId) {
		return recentTwentyMatchRepository.findByMemberId(memberId)
			.orElseThrow(RecentTwentyMatchNotFoundException::new);
	}

	// recentTwentyMatch 생성
	@Transactional
	public void createNewRecentTwentyMatch(RecentTwentyMatchDto recentTwentyMatchDto) {

		RecentTwentyMatch recentTwentyMatch = RecentTwentyMatch.builder()
			.memberId(recentTwentyMatchDto.getMemberId())
			.sumKills(recentTwentyMatchDto.getSumKills())
			.sumDeaths(recentTwentyMatchDto.getSumDeaths())
			.sumAssists(recentTwentyMatchDto.getSumAssists())
			.championStats(recentTwentyMatchDto.getChampionStats())
			.winRate(recentTwentyMatchDto.getWinRate())
			.build();

		recentTwentyMatchRepository.save(recentTwentyMatch);
	}

	// recentTwentyMatch 업데이트
	@Transactional
	public void updateRecentTwentyMatch(RecentTwentyMatchDto recentTwentyMatchDto) {
		RecentTwentyMatch recentTwentyMatch = getRecentTwentyMatchByMemberId(recentTwentyMatchDto.getMemberId());
		recentTwentyMatch.update(
			recentTwentyMatchDto.getSumKills(),
			recentTwentyMatchDto.getSumDeaths(),
			recentTwentyMatchDto.getSumAssists(),
			recentTwentyMatchDto.getChampionStats(),
			recentTwentyMatchDto.getWinRate()
		); // 영속성 상태에서 Dirty Checking을 해 자동으로 db에 커밋됨
	}
}
