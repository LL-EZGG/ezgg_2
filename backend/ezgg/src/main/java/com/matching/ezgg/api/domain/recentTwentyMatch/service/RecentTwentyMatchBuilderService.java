package com.matching.ezgg.api.domain.recentTwentyMatch.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.matching.ezgg.api.domain.match.entity.Match;
import com.matching.ezgg.api.domain.match.service.MatchService;
import com.matching.ezgg.api.domain.memberInfo.entity.MemberInfo;
import com.matching.ezgg.api.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.api.domain.recentTwentyMatch.ChampionStat;
import com.matching.ezgg.api.dto.RecentTwentyMatchDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentTwentyMatchBuilderService {

	private final MemberInfoService memberInfoService;
	private final MatchService matchService;

	// recentTwentyMatchDto 생성
	public RecentTwentyMatchDto buildDto(String puuid) {
		log.info("recentTwentyMatch 계산 시작");

		MemberInfo memberInfo = memberInfoService.getMemberInfoByPuuid(puuid);
		// matchIds 조회
		List<String> matchIds = memberInfo.getMatchIds();
		// member_Id 조회
		Long memberId = memberInfo.getMemberId();

		// recentTwentyMatch에 들어갈 값 계산(sumKills, sumDeaths, sumAssists, wins, losses, allChampionStat)
		AggregateResult result = calculateAggregateStatsFromMatches(matchIds, memberId);

		// 20경기 승률 계산
		int winRate = calculateWinRate(result.wins, result.losses);

		// most 3 챔피언들로 championStat 압축
		Map<String, ChampionStat> most3ChampionStats = extractMost3ChampionStats(result.allChampionStats);

		RecentTwentyMatchDto recentTwentyMatchDto = new RecentTwentyMatchDto();

		recentTwentyMatchDto.setMemberId(memberId);
		recentTwentyMatchDto.setSumKills(result.sumKills);
		recentTwentyMatchDto.setSumDeaths(result.sumDeaths);
		recentTwentyMatchDto.setSumAssists(result.sumAssists);
		recentTwentyMatchDto.setWinRate(winRate);
		recentTwentyMatchDto.setChampionStats(most3ChampionStats);

		log.info("recentTwentyMatch 계산 종료");
		return recentTwentyMatchDto;
	}

	// 계산 결과값 보관하기 위한 내부 클래스
	private static class AggregateResult {
		int sumKills = 0;
		int sumDeaths = 0;
		int sumAssists = 0;
		int wins = 0;
		int losses = 0;
		Map<String, ChampionStat> allChampionStats = new HashMap<>();
	}

	// recentTwentyMatch 계산 메서드
	private AggregateResult calculateAggregateStatsFromMatches(List<String> matchIds, Long memberId) {

		log.info("Riot Api Match 반복문 시작");
		AggregateResult result = new AggregateResult();

		// 최근 20 경기의 matchId들로 RecentTwentyMatch 업데이트
		for (String matchId : matchIds) {
			Match match = matchService.getMatchByMemberIdAndRiotMatchId(memberId, matchId);

			result.sumKills += match.getKills();
			result.sumDeaths += match.getDeaths();
			result.sumAssists += match.getAssists();

			if (Boolean.TRUE.equals(match.getWin())) {
				result.wins++;
			}else {
				result.losses++;
			}

			// championStat 업데이트
			result.allChampionStats
				.computeIfAbsent(match.getChampionName(),
					name -> ChampionStat.builder()
						.championName(name)
						.build())// 해당 챔피언이 처음으로 기록될때만 ChampionStat 객체 생성. 있을 시에는 해당 championStat 객체에 updateByMatch메서드 바로 적용
				.updateByMatch(match);
		}

		return result;
	}

	// 20경기 승률 계산 메서드
	private int calculateWinRate(int wins, int losses) {
		int total = wins + losses;
		return total == 0 ? 0 : (wins * 100) / total;
	}

	// 모스트 3 챔피언 계산 메서드
	private Map<String, ChampionStat> extractMost3ChampionStats(Map<String, ChampionStat> allChampionStats) {

		// most 3를 정할때, 20경기 중 동일한 판수를 가진 챔피언이 있으면, kda가 높은순으로 선택
		return allChampionStats.values().stream()
			.sorted(Comparator
				.comparingInt(ChampionStat::getTotal).reversed()
				.thenComparingDouble(ChampionStat::calculateKda).reversed())
			.limit(3)
			.collect(Collectors.toMap(
				ChampionStat::getChampionName, // key 값
				Function.identity(), // value 값(championStat)
				(a, b) -> a, // key 중복 발생 시 첫번째를 선택
				LinkedHashMap::new // 모스트 1,2,3 순서가 유지되도록 LinkedHashMap 사용
			));
	}
}
