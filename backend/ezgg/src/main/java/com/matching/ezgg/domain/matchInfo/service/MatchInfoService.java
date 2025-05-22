package com.matching.ezgg.domain.matchInfo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.domain.matchInfo.entity.MatchInfo;
import com.matching.ezgg.domain.matchInfo.repository.MatchInfoRepository;
import com.matching.ezgg.domain.riotApi.dto.MatchDto;
import com.matching.ezgg.global.exception.MatchNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchInfoService {
	private final MatchInfoRepository matchInfoRepository;

	@Transactional
	public void save(MatchDto matchDto) {
		log.info("match 저장 시작: {}", matchDto.getRiotMatchId());
		MatchInfo matchInfo = MatchInfo.builder()
			.memberId(matchDto.getMemberId())
			.riotMatchId(matchDto.getRiotMatchId())
			.kills(matchDto.getKills())
			.deaths(matchDto.getDeaths())
			.assists(matchDto.getAssists())
			.teamPosition(matchDto.getTeamPosition())
			.championName(matchDto.getChampionName())
			.win(matchDto.isWin())
			.matchAnalysis(matchDto.getMatchAnalysis())
			.build();

		matchInfoRepository.save(matchInfo);
		log.info("match 저장 종료: {}", matchDto.getRiotMatchId());
	}

	public MatchInfo getMatchByMemberIdAndRiotMatchId(Long memberId, String matchId) {
		return matchInfoRepository.findByMemberIdAndRiotMatchId(memberId, matchId)
			.orElseThrow(MatchNotFoundException::new);
	}
}
