package com.matching.ezgg.domain.matchInfo.matchKeyword.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JugMatchParsingDto {
	private Float visionScoreAdvantageLaneOpponent; // ≥0
	private Integer epicMonsterSteals; // ≥2
	private Integer enemyJungleMonsterKills; // ≥20
	private Integer riftHeraldTakedowns; // 합계 > 상대팀
	private Integer dragonKills;
	private Integer baronKills;
	private Boolean firstBloodKill; // true

}
