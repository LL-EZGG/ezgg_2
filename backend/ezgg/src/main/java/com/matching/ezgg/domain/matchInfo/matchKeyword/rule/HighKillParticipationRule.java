package com.matching.ezgg.domain.matchInfo.matchKeyword.rule;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;

public class HighKillParticipationRule implements KeywordRule {
	@Override
	public Boolean matchWithRule(GlobalMatchParsingDto globalMatchParsingDto, Lane lane) {
		Lane.Criteria criteria = lane.getCriteria();
		return criteria!=null && globalMatchParsingDto.getKillParticipation() >= criteria.killParticipation();
	}

	@Override
	public GlobalKeyword getKeyword() {
		return GlobalKeyword.HIGH_KILL_PARTICIPATION;
	}
}
