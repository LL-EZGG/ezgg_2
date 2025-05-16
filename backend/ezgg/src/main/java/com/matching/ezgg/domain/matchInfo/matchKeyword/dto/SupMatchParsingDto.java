package com.matching.ezgg.domain.matchInfo.matchKeyword.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SupMatchParsingDto {
	private Double visionScoreAdvantageLaneOpponent;	//0.5 이상
	private Integer	saveAllyFromDeath; //1 이상
	private Integer	wardPlaced;
	private Integer	gameDuration; //wardPlaced/게임시간= 분당 1.2 이상
}
