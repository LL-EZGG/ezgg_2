package com.matching.ezgg.domain.matchInfo.matchKeyword.service;

import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.MatchKeywordDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.entity.MatchKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;
import com.matching.ezgg.domain.matchInfo.matchKeyword.repository.MatchKeywordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

	private final MatchKeywordRepository matchKeywordRepository;

	public MatchKeyword createMatchKeyword(String keywordDescription, Lane lane, String matchId, Long memberId) {
		log.info("MatchKeyword 생성 완료: {}, {}", lane, keywordDescription);
		return MatchKeyword.builder()
			.memberId(memberId)
			.keyword(keywordDescription)
			.lane(lane)
			.riotMatchId(matchId)
			.build();
	}

	public void saveMatchKeyword(MatchKeyword matchKeyword) {
		MatchKeyword savedMatchKeyword = matchKeywordRepository.save(matchKeyword);
		MatchKeywordDto matchKeywordDto = MatchKeywordDto.toDto(savedMatchKeyword);
		log.info("MatchKeyword 저장 완료: {}", matchKeywordDto.toString());
	}

}
