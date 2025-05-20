package com.matching.ezgg.domain.matchInfo.matchKeyword.rule;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;

public class InvadingRule implements KeywordRule{
	@Override
	public Boolean matchWithRule(GlobalMatchParsingDto globalMatchParsingDto, Lane lane) {
		return globalMatchParsingDto.getTakedownsBeforeJungleMinionSpawn() >= 2;
	}

	@Override
	public GlobalKeyword getKeyword() {
		return GlobalKeyword.INVADING;
	}
}
