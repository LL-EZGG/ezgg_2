package com.matching.ezgg.domain.matchInfo.matchKeyword.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GlobalMatchParsingDto {
	private Float killParticipation; //≥ 40% ≥ 60% ≥ 50% ≥ 50% ≥ 60%
	private Float kda; //≥ 4.5 ≥ 4.0 ≥ 4.5.≥ 4.5 ≥ 3.5
	private Float teamDamagePercentage; //팀 내 teamDamagePercent 1위 && win = True
	private Boolean win;
	private Float damagePerMinute; //≥ 800 ≥ 600 ≥ 800 ≥ 900 ≥ 250
	private Integer maxLevelLeadLaneOpponent; //2 이상
	private Integer immobilizeAndKillWithAlly; //4 이상
	private Boolean	gameEndedInSurrender; //False
	private Integer	multiKillOneSpell; //1 이상
}
