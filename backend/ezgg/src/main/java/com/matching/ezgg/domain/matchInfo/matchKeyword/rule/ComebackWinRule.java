package com.matching.ezgg.domain.matchInfo.matchKeyword.rule;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;

public class ComebackWinRule implements KeywordRule {
	@Override
	public Boolean matchWithRule(GlobalMatchParsingDto globalMatchParsingDto, Lane lane) {
		return globalMatchParsingDto.getWin() == Boolean.TRUE && globalMatchParsingDto.getLostAnInhibitor() >= 2;
	}

	@Override
	public GlobalKeyword getKeyword() {
		return GlobalKeyword.COMEBACK_WIN;
	}
}
