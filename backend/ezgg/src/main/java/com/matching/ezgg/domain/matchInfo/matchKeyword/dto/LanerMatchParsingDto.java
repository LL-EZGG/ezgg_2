package com.matching.ezgg.domain.matchInfo.matchKeyword.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
 public class LanerMatchParsingDto {
	private Integer killsOnOtherLanesEarlyJungleAsLaner; // ≥1
	private Integer takedownsInAllLanesEarlyJungleAsLaner; // ≥1
	private Integer turretPlatesTaken; // > opponentTurretPlatesTaken
	private Integer turretsLost; // turretPlatesTaken ≥2 && turretsLost < opponentTurretsLost
	private Integer killsUnderOwnTurret; // ≥2
	private Integer killsNearEnemyTurret; // ≥2
	private Integer maxCsAdvantageOnLaneOpponent; // ≥20
	private Boolean firstBloodKill; // true
}
