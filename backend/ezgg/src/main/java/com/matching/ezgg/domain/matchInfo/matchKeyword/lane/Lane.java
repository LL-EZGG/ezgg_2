package com.matching.ezgg.domain.matchInfo.matchKeyword.lane;

import lombok.Getter;

@Getter
public enum Lane {
	TOP(new Criteria(0.4, 4.5, 800.0)),
	JUNGLE(new Criteria(0.6, 4.0, 600.0)),
	MIDDLE(new Criteria(0.5, 4.5, 800.0)),
	BOTTOM(new Criteria(0.5, 4.5, 900.0)),
	UTILITY(new Criteria(0.6, 3.5, 250.0));

	public final Criteria criteria;

	Lane(Criteria criteria) {
		this.criteria = criteria;
	}

	public static record Criteria(Double killParticipation, Double kda, Double damagePerMinute) {}

}
