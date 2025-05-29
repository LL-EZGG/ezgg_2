package com.matching.ezgg.domain.matchInfo.matchKeyword.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
import com.matching.ezgg.domain.riotApi.util.MatchMapper;

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
	 * Analyzer를 통해 Global 키워드와 포지션별 키워드를 부여하여 키워드 리스트를 생성하는 메서드
	 * @param rawJson
	 * @param teamPosition
	 * @param puuid
	 * @param matchId
	 * @param memberId
	 * @return 한 match에 부여된 키워드 리스트 List<String>
	 */

	public List<String> giveMatchKeyword(String rawJson, String teamPosition, String puuid, String matchId,
		Long memberId) {
		GlobalMatchParsingDto globalMatchParsingDto = matchMapper.toGlobalMatchParsingDto(rawJson, puuid);

		//global 키워드 리스트 생성
		List<String> globalKeywords = globalKeywordAnalyzer.analyze(globalMatchParsingDto, teamPosition, matchId, memberId);

		//포지션별 키워드 리스트 생성
		List<String> positionKeywords = switch (teamPosition) {
			case ("TOP"), ("MIDDLE"), ("BOTTOM") -> {
				LanerMatchParsingDto lanerMatchParsingDto = matchMapper.toLanerMatchParsingDto(rawJson, puuid,
					teamPosition);
				yield lanerKeywordAnalyzer.analyze(lanerMatchParsingDto, teamPosition, matchId, memberId);
			}
			case ("JUNGLE") -> {
				JugMatchParsingDto jugMatchParsingDto = matchMapper.toJugMatchParsingDto(rawJson, puuid);
				yield jugKeywordAnalyzer.analyze(jugMatchParsingDto, teamPosition, matchId, memberId);
			}
			case ("UTILITY") -> {
				SupMatchParsingDto supMatchParsingDto = matchMapper.toSupMatchParsingDto(rawJson, puuid);
				yield supKeywordAnalyzer.analyze(supMatchParsingDto, teamPosition, matchId, memberId);
			}
			default -> List.of();
		};
		return new ArrayList<>(Stream.concat(globalKeywords.stream(), positionKeywords.stream()).toList());
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

	// @Transactional
	// public String buildMatchKeywordAnalysis(MatchDto matchDto, String rawJson, String puuid, String matchId,
	// 	Long memberId) {
	// 	StringBuilder analysis = new StringBuilder();
	// 	analysis.append(keywordService.extractChampionRole(matchDto.getChampionName()));
	// 	analysis.append(giveMatchKeyword(rawJson, matchDto.getTeamPosition(), puuid, matchId, memberId));
	// 	return analysis.toString();
	// }
}
