package com.matching.ezgg.domain.recentTwentyMatch.dto;

import java.util.Map;

import com.matching.ezgg.domain.recentTwentyMatch.ChampionStat;
import com.matching.ezgg.domain.recentTwentyMatch.entity.RecentTwentyMatch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentTwentyMatchDto {
	private long memberId;
	private int sumKills;
	private int sumDeaths;
	private int sumAssists;
	private Map<String, ChampionStat> championStats;
	private int winRate;

	public static RecentTwentyMatchDto toDto(RecentTwentyMatch recentTwentyMatch) {
		return RecentTwentyMatchDto.builder()
			.sumKills(recentTwentyMatch.getSumKills())
			.sumDeaths(recentTwentyMatch.getSumDeaths())
			.sumAssists(recentTwentyMatch.getSumAssists())
			.championStats(recentTwentyMatch.getChampionStats())
			.winRate(recentTwentyMatch.getWinRate())
			.build();
	}
}
