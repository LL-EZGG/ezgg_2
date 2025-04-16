package com.matching.ezgg.api.domain.recentTwentyMatch.entity;

import java.util.Map;

import com.matching.ezgg.api.domain.recentTwentyMatch.ChampionStat;
import com.matching.ezgg.common.ChampionStatsConvert;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecentTwentyMatch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// @ManyToOne(fetch = FetchType.LAZY)
	// private Member member;
	private String recentRiotMatchId;
	private Long recentKills;
	private Long recentDeaths;
	private Long recentAssists;

	@Convert(converter = ChampionStatsConvert.class)
	private Map<String, ChampionStat> championStats;

	@Builder
	public RecentTwentyMatch(String recentRiotMatchId, Long recentKills, Long recentDeaths, Long recentAssists,
		Map<String, ChampionStat> championStats) {
		this.recentRiotMatchId = recentRiotMatchId;
		this.recentKills = recentKills;
		this.recentDeaths = recentDeaths;
		this.recentAssists = recentAssists;
		this.championStats = championStats;
	}
}
