package com.matching.ezgg.domain.matchInfo.dto;

import com.matching.ezgg.domain.matchInfo.entity.MatchInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchInfoDto {
	private Long memberId;
	private String riotMatchId;
	private Integer kills;
	private Integer deaths;
	private Integer assists;
	private String teamPosition;
	private String championName;
	private Boolean win;
	private String matchAnalysis = "";

	public static MatchInfoDto toMatchInfoDto(MatchInfo matchInfo) {
		return MatchInfoDto.builder()
			.memberId(matchInfo.getId())
			.riotMatchId(matchInfo.getRiotMatchId())
			.kills(matchInfo.getKills())
			.deaths(matchInfo.getDeaths())
			.assists(matchInfo.getAssists())
			.teamPosition(matchInfo.getTeamPosition())
			.championName(matchInfo.getChampionName())
			.win(matchInfo.getWin())
			.matchAnalysis(matchInfo.getMatchAnalysis())
			.build();
	}
}
