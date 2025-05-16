package com.matching.ezgg.domain.matchInfo.matchKeyword.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JugMatchParsingDto {
	private Double visionScoreAdvantageLaneOpponent; // ≥0
	private Integer epicMonsterSteals; // ≥2
	private Integer enemyJungleMonsterKills; // ≥20
	private Integer riftHeraldTakedowns; // riftHeraldTakedowns + dragonTakedowns + baronTakedowns = 상대보다 ↑
	private Integer dragonTakedowns;
	private Integer baronTakedowns;
	private Integer opponentRiftHeraldTakeDowns;
	private Integer opponentDragonTakedowns;
	private Integer opponentBaronTakedowns;
	private Boolean firstBloodKill; // true

}
