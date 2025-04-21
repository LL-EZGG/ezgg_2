package com.matching.ezgg.api.domain.match.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.api.dto.MatchDto;
import com.matching.ezgg.api.domain.match.entity.Match;
import com.matching.ezgg.api.domain.match.repository.MatchRepository;
import com.matching.ezgg.global.exception.MatchNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {
	private final MatchRepository matchRepository;

	@Transactional
	public void save(MatchDto matchDto) {
		log.info("match 저장 시작: {}", matchDto.getRiotMatchId());
		Match match = Match.builder()
			.memberId(matchDto.getMemberId())
			.riotMatchId(matchDto.getRiotMatchId())
			.kills(matchDto.getKills())
			.deaths(matchDto.getDeaths())
			.assists(matchDto.getAssists())
			.teamPosition(matchDto.getTeamPosition())
			.championName(matchDto.getChampionName())
			.win(matchDto.isWin())
			.build();

		matchRepository.save(match);
		log.info("match 저장 종료: {}", matchDto.getRiotMatchId());
	}

	public Match getMatchByMemberIdAndRiotMatchId(Long memberId, String matchId) {
		return matchRepository.findByMemberIdAndRiotMatchId(memberId, matchId)
			.orElseThrow(MatchNotFoundException::new);
	}
}
