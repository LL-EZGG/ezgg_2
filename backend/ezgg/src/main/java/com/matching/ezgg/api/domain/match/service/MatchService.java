package com.matching.ezgg.api.domain.match.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.api.dto.MatchDto;
import com.matching.ezgg.api.domain.match.entity.Match;
import com.matching.ezgg.api.domain.match.repository.MatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {
	private final MatchRepository matchRepository;

	@Transactional
	public void save(MatchDto matchDto) {
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
	}
}
