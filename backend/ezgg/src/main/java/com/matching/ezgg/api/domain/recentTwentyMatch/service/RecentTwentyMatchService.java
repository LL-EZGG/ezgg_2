package com.matching.ezgg.api.domain.recentTwentyMatch.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.api.domain.recentTwentyMatch.dto.RecentTwentyMatchDto;
import com.matching.ezgg.api.domain.recentTwentyMatch.entity.RecentTwentyMatch;
import com.matching.ezgg.api.domain.recentTwentyMatch.repository.RecentTwentyMatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecentTwentyMatchService {

	private final RecentTwentyMatchRepository recentTwentyMatchRepository;

	@Transactional
	public void save(RecentTwentyMatchDto recentTwentyMatchDto) {
		try {
			RecentTwentyMatch recentTwentyMatch = RecentTwentyMatch.builder()
				.memberId(recentTwentyMatchDto.getMemberId())
				.sumKills(recentTwentyMatchDto.getSumKills())
				.sumDeaths(recentTwentyMatchDto.getSumDeaths())
				.sumAssists(recentTwentyMatchDto.getSumAssists())
				.championStats(recentTwentyMatchDto.getChampionStats())
				.build();

			recentTwentyMatchRepository.save(recentTwentyMatch);
		} catch (Exception e) {
			e.printStackTrace();//FIXME 예외처리 제대로 하고 삭제
			//TODO 예외 경우 여러가지 판단해서 커스텀Exception 생성하고 각각 ErrorResponse 리턴하는 예외처리
		}
	}
}
