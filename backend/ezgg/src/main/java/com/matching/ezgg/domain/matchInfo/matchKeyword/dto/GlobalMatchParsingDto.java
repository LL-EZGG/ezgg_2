package com.matching.ezgg.domain.matchInfo.matchKeyword.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GlobalMatchParsingDto {
	private Double killParticipation; //≥ 40% ≥ 60% ≥ 50% ≥ 50% ≥ 60%
	private Double kda; //≥ 4.5 ≥ 4.0 ≥ 4.5.≥ 4.5 ≥ 3.5
	private Double teamDamagePercentage; //팀 내 teamDamagePercent 1위 && win = True
	private Boolean win;
	private Double damagePerMinute; //≥ 800 ≥ 600 ≥ 800 ≥ 900 ≥ 250
	private Integer maxLevelLeadLaneOpponent; //2 이상
	private Integer immobilizeAndKillWithAlly; //4 이상
	private Boolean	gameEndedInSurrender; //False
	private Integer	multiKillOneSpell; //1 이상
	private Integer	pickKillWithAlly; //10 이상
	private Integer lostAnInhibitor; // lostAnInhibitor>=2 && win=True
	private Integer	takedownsBeforeJungleMinionSpawn; // > 상대 pickKillWithAlly
}
