package com.matching.ezgg.domain.matchInfo.matchKeyword.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matchInfo.matchKeyword.analyzer.KeywordAnalyzer;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.JugMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.LanerMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.SupMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.JugKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.LanerKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.SupKeyword;
import com.matching.ezgg.domain.riotApi.dto.MatchDto;
import com.matching.ezgg.domain.riotApi.util.MatchMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeywordAnalyzerService {

	private final MatchMapper matchMapper;
	private final KeywordService keywordService;
	@Qualifier("globalKeywordAnalyzer")
	private final KeywordAnalyzer<GlobalMatchParsingDto, GlobalKeyword> globalKeywordAnalyzer;
	@Qualifier("lanerKeywordAnalyzer")
	private final KeywordAnalyzer<LanerMatchParsingDto, LanerKeyword> lanerKeywordAnalyzer;
	@Qualifier("jugKeywordAnalyzer")
	private final KeywordAnalyzer<JugMatchParsingDto, JugKeyword> jugKeywordAnalyzer;
	@Qualifier("supKeywordAnalyzer")
	private final KeywordAnalyzer<SupMatchParsingDto, SupKeyword> supKeywordAnalyzer;

	/**
	 * Analyzer를 통해 Global 키워드와 포지션별 키워드를 부여하여 한 줄 평가를 생성하는 메서드
	 * @param rawJson
	 * @param teamPosition
	 * @param puuid
	 * @param matchId
	 * @param memberId
	 * @return 한 match에 부여된 모든 평가를 합친 String
	 */

	private String giveMatchKeyword(String rawJson, String teamPosition, String puuid, String matchId, Long memberId) {
		GlobalMatchParsingDto globalMatchParsingDto = matchMapper.toGlobalMatchParsingDto(rawJson, puuid);
		StringBuilder matchAnalysis = new StringBuilder();
		//global 키워드에 대한 한 줄 평가 생성
		matchAnalysis.append(globalKeywordAnalyzer.analyze(globalMatchParsingDto, teamPosition, matchId, memberId));

		//포지션별 키워드에 대한 한 줄 평가 생성
		switch (teamPosition) {
			case ("TOP"), ("MIDDLE"), ("BOTTOM"):
				LanerMatchParsingDto lanerMatchParsingDto = matchMapper.toLanerMatchParsingDto(rawJson, puuid,
					teamPosition);
				matchAnalysis.append(
					lanerKeywordAnalyzer.analyze(lanerMatchParsingDto, teamPosition, matchId, memberId));
				break;
			case ("JUNGLE"):
				JugMatchParsingDto jugMatchParsingDto = matchMapper.toJugMatchParsingDto(rawJson, puuid);
				matchAnalysis.append(jugKeywordAnalyzer.analyze(jugMatchParsingDto, teamPosition, matchId, memberId));
				break;
			case ("UTILITY"):
				SupMatchParsingDto supMatchParsingDto = matchMapper.toSupMatchParsingDto(rawJson, puuid);
				matchAnalysis.append(supKeywordAnalyzer.analyze(supMatchParsingDto, teamPosition, matchId, memberId));
				break;
		}
		return matchAnalysis.toString();
	}

	/**
	 * 한 매치의 챔피언 역할과 자연어 평가를 합쳐 한 줄 평가를 만드는 메서드
	 * @param matchDto
	 * @param rawJson
	 * @param puuid
	 * @param matchId
	 * @param memberId
	 * @return 한 줄 평가 String
	 */

	@Transactional
	public String buildMatchKeywordAnalysis(MatchDto matchDto, String rawJson, String puuid, String matchId,
		Long memberId) {
		StringBuilder analysis = new StringBuilder();
		analysis.append(keywordService.extractChampionRole(matchDto.getChampionName()));
		analysis.append(giveMatchKeyword(rawJson, matchDto.getTeamPosition(), puuid, matchId, memberId));
		return analysis.toString();
	}
}
