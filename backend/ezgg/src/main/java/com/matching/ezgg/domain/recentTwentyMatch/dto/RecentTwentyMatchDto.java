package com.matching.ezgg.domain.recentTwentyMatch.dto;

import java.util.Map;

import com.matching.ezgg.domain.recentTwentyMatch.entity.RecentTwentyMatch;
import com.matching.ezgg.domain.recentTwentyMatch.entity.model.ChampionStat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
	private String topAnalysis;
	private String jugAnalysis;
	private String midAnalysis;
	private String adAnalysis;
	private String supAnalysis;

	public static RecentTwentyMatchDto toDto(RecentTwentyMatch recentTwentyMatch) {
		return RecentTwentyMatchDto.builder()
			.sumKills(recentTwentyMatch.getSumKills())
			.sumDeaths(recentTwentyMatch.getSumDeaths())
			.sumAssists(recentTwentyMatch.getSumAssists())
			.championStats(recentTwentyMatch.getChampionStats())
			.winRate(recentTwentyMatch.getWinRate())
			.topAnalysis(recentTwentyMatch.getTopAnalysis())
			.jugAnalysis(recentTwentyMatch.getJugAnalysis())
			.midAnalysis(recentTwentyMatch.getMidAnalysis())
			.adAnalysis(recentTwentyMatch.getAdAnalysis())
			.supAnalysis(recentTwentyMatch.getSupAnalysis())
			.build();
	}
}
