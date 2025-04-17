package com.matching.ezgg.api.domain.recentTwentyMatch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ChampionStat {

	private String championName;
	private int kills;
	private int deaths;
	private int assists;
	// todo 챔피언별 승률 (총 판수 / 총 승리 횟수)

	@Builder
	public ChampionStat(String championName, int kills, int deaths, int assists) {
		this.championName = championName;
		this.kills = kills;
		this.deaths = deaths;
		this.assists = assists;
	}
}
