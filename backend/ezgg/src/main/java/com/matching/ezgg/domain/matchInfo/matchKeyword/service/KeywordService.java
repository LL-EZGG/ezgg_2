package com.matching.ezgg.domain.matchInfo.matchKeyword.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matchInfo.matchKeyword.championInfo.ChampionBasicInfo;
import com.matching.ezgg.domain.matchInfo.matchKeyword.championInfo.ChampionRole;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.MatchKeywordDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.entity.MatchKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;
import com.matching.ezgg.domain.matchInfo.matchKeyword.repository.MatchKeywordRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

	private final MatchKeywordRepository matchKeywordRepository;

	@Transactional
	public MatchKeyword createMatchKeyword(String keywordDescription, Lane lane, String matchId, Long memberId) {
		log.info("[INFO] MatchKeyword 생성 완료: {}, {}", lane, keywordDescription);
		return MatchKeyword.builder()
			.memberId(memberId)
			.keyword(keywordDescription)
			.lane(lane)
			.riotMatchId(matchId)
			.build();
	}

	@Transactional
	public void saveMatchKeyword(MatchKeyword matchKeyword) {
		MatchKeyword savedMatchKeyword = matchKeywordRepository.save(matchKeyword);
		MatchKeywordDto matchKeywordDto = MatchKeywordDto.toDto(savedMatchKeyword);
		log.info("[INFO] MatchKeyword 저장 완료: {}", matchKeywordDto.toString());
	}

	/**
	 * 챔피언별 역할을 ChampionBasicInfo enum에서 추출하는 메서드
	 * @param championName
	 * @return 챔피언별 역할 String
	 */

	public String extractChampionRole(String championName) {
		StringBuilder analysis = new StringBuilder();
		List<ChampionRole> championRoles = ChampionBasicInfo.valueOf(
			championName.replaceAll("[^a-zA-Z0-9]", "").trim().toUpperCase()).getChampionRoles();

		for (ChampionRole role : championRoles) {
			analysis.append(role.getKoreanRoleName()).append(",");
		}
		return analysis.toString();
	}
}