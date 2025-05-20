package com.matching.ezgg.domain.matchInfo.matchKeyword.rule;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;

public class NeverGiveUpRule implements KeywordRule {
	@Override
	public Boolean matchWithRule(GlobalMatchParsingDto globalMatchParsingDto, Lane lane) {
		return globalMatchParsingDto.getGameEndedInSurrender() == Boolean.FALSE;
	}

	@Override
	public GlobalKeyword getKeyword() {
		return GlobalKeyword.NEVER_GIVES_UP;
	}
}
