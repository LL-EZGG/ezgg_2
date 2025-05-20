package com.matching.ezgg.domain.matchInfo.matchKeyword.service;

import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.MatchKeywordDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.entity.MatchKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;
import com.matching.ezgg.domain.matchInfo.matchKeyword.repository.MatchKeywordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GlobalKeywordService {

	private final MatchKeywordRepository matchKeywordRepository;

	public MatchKeyword createMatchKeyword(GlobalKeyword globalKeyword, Lane lane, String matchId, Long memberId) {
		return of(globalKeyword, lane, matchId, memberId);
	}

	public void saveMatchKeyword(MatchKeyword matchKeyword) {
		MatchKeyword savedMatchKeyword = matchKeywordRepository.save(matchKeyword);
		MatchKeywordDto matchKeywordDto = MatchKeywordDto.toDto(savedMatchKeyword);
		log.info("MatchKeyword 저장 완료: {}", matchKeywordDto.toString());
	}

	public static MatchKeyword of(GlobalKeyword keyword, Lane lane, String matchId, Long memberId) {
		return MatchKeyword.builder()
			.memberId(memberId)
			.keyword(keyword.getDescription())
			.lane(lane)
			.riotMatchId(matchId)
			.build();
	}
}
