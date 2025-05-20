package com.matching.ezgg.domain.matchInfo.matchKeyword.rule;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;

public interface KeywordRule {
	Boolean matchWithRule(GlobalMatchParsingDto globalMatchParsingDto, Lane lane);
	GlobalKeyword getKeyword();
}
