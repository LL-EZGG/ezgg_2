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
import com.matching.ezgg.domain.riotApi.util.MatchMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KeywordAnalyzerService {

	private final MatchMapper matchMapper;
	@Qualifier("globalKeywordAnalyzer")
	KeywordAnalyzer<GlobalMatchParsingDto, GlobalKeyword> globalKeywordAnalyzer;
	@Qualifier("lanerKeywordAnalyzer")
	KeywordAnalyzer<LanerMatchParsingDto, LanerKeyword> lanerKeywordAnalyzer;
	@Qualifier("jugKeywordAnalyzer")
	KeywordAnalyzer<JugMatchParsingDto, JugKeyword> jugKeywordAnalyzer;
	@Qualifier("supKeywordAnalyzer")
	KeywordAnalyzer<SupMatchParsingDto, SupKeyword> supKeywordAnalyzer;

	public KeywordAnalyzerService(MatchMapper matchMapper,
		KeywordAnalyzer<GlobalMatchParsingDto, GlobalKeyword> globalKeywordAnalyzer,
		KeywordAnalyzer<LanerMatchParsingDto, LanerKeyword> lanerKeywordAnalyzer,
		KeywordAnalyzer<JugMatchParsingDto, JugKeyword> jugKeywordAnalyzer,
		KeywordAnalyzer<SupMatchParsingDto, SupKeyword> supKeywordAnalyzer) {
		this.matchMapper = matchMapper;
		this.globalKeywordAnalyzer = globalKeywordAnalyzer;
		this.lanerKeywordAnalyzer = lanerKeywordAnalyzer;
		this.jugKeywordAnalyzer = jugKeywordAnalyzer;
		this.supKeywordAnalyzer = supKeywordAnalyzer;
	}

	public String giveMatchKeyword(String rawJson, String teamPosition, String puuid, String matchId, Long memberId) {
		GlobalMatchParsingDto globalMatchParsingDto = matchMapper.toGlobalMatchParsingDto(rawJson, puuid);

		StringBuilder matchAnalysis = new StringBuilder();
		//global 키워드 부여
		matchAnalysis.append(globalKeywordAnalyzer.analyze(globalMatchParsingDto, teamPosition, matchId, memberId));

		//포지션별 키워드 부여
		switch (teamPosition) {
			case ("TOP"), ("MIDDLE"), ("BOTTOM"):
				LanerMatchParsingDto lanerMatchParsingDto = matchMapper.toLanerMatchParsingDto(rawJson, puuid,
					teamPosition);
				matchAnalysis.append(
					lanerKeywordAnalyzer.analyze(lanerMatchParsingDto, teamPosition, matchId, memberId));
				log.info(lanerMatchParsingDto.toString());
				break;
			case ("JUNGLE"):
				JugMatchParsingDto jugMatchParsingDto = matchMapper.toJugMatchParsingDto(rawJson, puuid);
				matchAnalysis.append(jugKeywordAnalyzer.analyze(jugMatchParsingDto, teamPosition, matchId, memberId));
				log.info(jugMatchParsingDto.toString());
				break;
			case ("UTILITY"):
				SupMatchParsingDto supMatchParsingDto = matchMapper.toSupMatchParsingDto(rawJson, puuid);
				matchAnalysis.append(supKeywordAnalyzer.analyze(supMatchParsingDto, teamPosition, matchId, memberId));
				log.info(supMatchParsingDto.toString());
				break;
		}
		return matchAnalysis.toString();
	}

}
