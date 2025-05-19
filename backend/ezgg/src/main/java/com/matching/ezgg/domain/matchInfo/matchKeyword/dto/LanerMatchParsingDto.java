package com.matching.ezgg.domain.matchInfo.matchKeyword.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
 public class LanerMatchParsingDto {
	private Integer killsOnOtherLanesEarlyJungleAsLaner; // ≥1
	private Integer getTakedownsInAllLanesEarlyJungleAsLaner; // ≥1
	private Integer turretPlatesTaken; // > opponentTurretPlatesTaken
	private Integer opponentTurretPlatesTaken;
	private Integer turretsLost; // turretKills ≥ 2 && turretLost < opponentTurretsLost
	private Integer opponentTurretsLost;
	private Integer turretKills;
	private Integer killsUnderOwnTurret; // ≥2
	private Integer killsNearEnemyTurret; // ≥2
	private Double maxCsAdvantageOnLaneOpponent; // ≥20
	private Boolean firstBloodKill; // true
	private Integer lostAnInhibitor; // lostAnInhibitor>=2 && win=True
	private Integer killAfterHiddenWithAlly; // killAfterHiddenWithAlly>=2
}
