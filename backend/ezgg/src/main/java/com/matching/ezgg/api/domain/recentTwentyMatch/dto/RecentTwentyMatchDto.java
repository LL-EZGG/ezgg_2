package com.matching.ezgg.api.domain.recentTwentyMatch.dto;

import java.util.Map;

import com.matching.ezgg.api.domain.recentTwentyMatch.ChampionStat;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecentTwentyMatchDto {//TODO 예시 DTO. 상황에 맞게 조정
	private long memberId;
	private int sumKills;
	private int sumDeaths;
	private int sumAssists;
	private Map<String, ChampionStat> championStats;
}
