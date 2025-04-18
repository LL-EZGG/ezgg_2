package com.matching.ezgg.api.domain.match.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.api.domain.match.dto.MatchDto;
import com.matching.ezgg.api.domain.match.entity.Match;
import com.matching.ezgg.api.domain.match.repository.MatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {
	private final MatchRepository matchRepository;


	@Transactional
	public void save(MatchDto matchDto) {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();//FIXME 예외처리 제대로 하고 삭제
			//TODO 예외 경우 여러가지 판단해서 커스텀Exception 생성하고 각각 ErrorResponse 리턴하는 예외처리
		}
	}
}
