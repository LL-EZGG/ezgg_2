package com.matching.ezgg.domain.matchInfo.matchKeyword.rule;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;

public class TeamFighterRule implements KeywordRule {
	@Override
	public Boolean matchWithRule(GlobalMatchParsingDto globalMatchParsingDto, Lane lane) {
		return globalMatchParsingDto.getMultiKillOneSpell() >= 1;
	}

	@Override
	public GlobalKeyword getKeyword() {
		return GlobalKeyword.TEAM_FIGHTER;
	}
}
