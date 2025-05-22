package com.matching.ezgg.domain.matchInfo.matchKeyword.analyzer;

import java.util.List;

import com.matching.ezgg.domain.matchInfo.matchKeyword.entity.MatchKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;
import com.matching.ezgg.domain.matchInfo.matchKeyword.rule.KeywordRule;
import com.matching.ezgg.domain.matchInfo.matchKeyword.service.KeywordService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeywordAnalyzer<T, K> {

	private final KeywordService keywordService;
	private final List<KeywordRule<T, K>> rules;

	public KeywordAnalyzer(KeywordService keywordService, List<KeywordRule<T, K>> rules) {
		this.keywordService = keywordService;
		this.rules = rules;
	}

	public String analyze(T matchParsingDto, String teamPosition, String matchId, Long memberId) {

		StringBuilder analysis = new StringBuilder();
		Lane lane;
		try {
			 lane = Lane.valueOf(teamPosition);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}

		//모든 global 규칙 확인
		for (KeywordRule<T, K> rule : rules) {
			//규칙에 부합하면
			if (rule.matchWithRule(matchParsingDto, lane)) {
				//키워드 생성 및 저장
				String keywordDescription = rule.getDescription();
				MatchKeyword matchKeyword = keywordService.createMatchKeyword(keywordDescription, lane, matchId, memberId);
				keywordService.saveMatchKeyword(matchKeyword);
				//자연어 평가 생성
				analysis.append(keywordDescription).append(",");
			}
		}
		log.info("Analysis: {}, {}", analysis, memberId);
		return analysis.toString();
	}

}