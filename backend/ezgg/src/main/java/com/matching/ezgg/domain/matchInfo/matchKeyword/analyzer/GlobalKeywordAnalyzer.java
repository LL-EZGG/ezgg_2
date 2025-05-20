package com.matching.ezgg.domain.matchInfo.matchKeyword.analyzer;

import java.util.List;

import org.springframework.stereotype.Component;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.ComebackWinRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.CooperativeRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.GoodSynergyRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.HighDamageRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.HighKdaRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.HighKillParticipationRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.InvadingRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.KeywordRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.LevelDiffRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.NeverGivesUpRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.TeamFighterRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.service.GlobalKeywordService;


@Component
public class GlobalKeywordAnalyzer {

	private final GlobalKeywordService globalKeywordService;
	private final List<KeywordRule> rules;

	public GlobalKeywordAnalyzer(GlobalKeywordService globalKeywordService) {
		this.globalKeywordService = globalKeywordService;
		this.rules = List.of(
			new HighKillParticipationRule(),
			new HighKdaRule(),
			new LevelDiffRule(),
			new GoodSynergyRule(),
			new NeverGivesUpRule(),
			new TeamFighterRule(),
			new CooperativeRule(),
			new InvadingRule(),
			new ComebackWinRule(),
			new HighDamageRule()
			//TODO
			// CLUTCH_WINNER("게임 캐리함"), //teamDamagePercent, win
			// SURVIVABILITY("생존 잘함"), //longestTimeSpentLiving
		);
	}

	public String analyze(GlobalMatchParsingDto globalMatchParsingDto, String teamPosition, String matchId, Long memberId) {

		StringBuilder analysis = new StringBuilder();
		Lane lane;
		try {
			 lane = Lane.valueOf(teamPosition);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}

		//모든 global 규칙 확인
		for (KeywordRule rule : rules) {
			//규칙에 부합하면
			if (rule.matchWithRule(globalMatchParsingDto, lane)) {
				//키워드 생성 및 저장
				GlobalKeyword keyword = rule.getKeyword();
				globalKeywordService.saveMatchKeyword(
					globalKeywordService.createMatchKeyword(keyword, lane, matchId, memberId)
				);
				//자연어 평가 생성
				analysis.append(keyword.getDescription());
			}
		}

		return analysis.toString();
	}

}