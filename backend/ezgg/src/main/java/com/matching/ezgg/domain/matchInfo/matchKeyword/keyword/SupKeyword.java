package com.matching.ezgg.domain.matchInfo.matchKeyword.keyword;

import lombok.Getter;

public enum SupKeyword {
	VISION_DOMINANCE("시야 장악"), //visionScoreAdvantageLaneOpponent
	WARDS_PLACED("시야 밝혀줌"), //wardsPlaced, gameDuration
	SAVE_ALLY("팀원 살림"); //saveAllyFromDeath

	@Getter
	private final String description;

	SupKeyword(String description) {
		this.description = description;
	}
}
