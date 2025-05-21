package com.matching.ezgg.domain.matchInfo.matchKeyword.rule.jugRule;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.JugMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.JugKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.KeywordRule;

public class ObjectiveStealerRule implements KeywordRule<JugMatchParsingDto, JugKeyword> {
	@Override
	public Boolean matchWithRule(JugMatchParsingDto jugMatchParsingDto, Lane lane) {
		return jugMatchParsingDto.getEpicMonsterSteals()>=2;
	}

	@Override
	public JugKeyword getKeyword() {
		return JugKeyword.OBJECTIVE_STEALER;
	}
}
