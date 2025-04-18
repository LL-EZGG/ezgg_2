package com.matching.ezgg.api.domain.recentTwentyMatch.entity;

import java.util.Map;

import com.matching.ezgg.api.domain.recentTwentyMatch.ChampionStat;
import com.matching.ezgg.common.ChampionStatsConvert;
import com.matching.ezgg.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recent_twenty_match")
public class RecentTwentyMatch extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "id", nullable = false)
	// @ToString.Exclude  // 순환 참조 방지
	// @EqualsAndHashCode.Exclude // 순환 참조 방지
	// private Member member;TODO

	@Column(name = "sum_kills", unique = false, nullable = true)
	private Integer sumKills;

	@Column(name = "sum_deaths", unique = false, nullable = true)
	private Integer sumDeaths;

	@Column(name = "sum_assists", unique = false, nullable = true)
	private Integer sumAssists;

	@Column(name = "champion_stats", unique = false, nullable = true)
	@Convert(converter = ChampionStatsConvert.class)
	private Map<String, ChampionStat> championStats;

	@Builder
	public RecentTwentyMatch(Integer sumKills, Integer sumDeaths, Integer sumAssists,
		Map<String, ChampionStat> championStats) {
		this.sumKills = sumKills;
		this.sumDeaths = sumDeaths;
		this.sumAssists = sumAssists;
		this.championStats = championStats;
	}
}
