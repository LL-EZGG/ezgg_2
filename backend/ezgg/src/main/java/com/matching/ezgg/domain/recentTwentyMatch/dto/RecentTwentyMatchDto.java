package com.matching.ezgg.domain.recentTwentyMatch.dto;

import java.util.Map;

import com.matching.ezgg.domain.recentTwentyMatch.ChampionStat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecentTwentyMatchDto {
	private long memberId;
	private int sumKills;
	private int sumDeaths;
	private int sumAssists;
	private Map<String, ChampionStat> championStats;
	private int winRate;
}
