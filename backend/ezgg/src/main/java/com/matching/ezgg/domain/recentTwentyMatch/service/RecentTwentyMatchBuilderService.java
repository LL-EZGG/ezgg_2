package com.matching.ezgg.domain.recentTwentyMatch.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.domain.matchInfo.entity.MatchInfo;
import com.matching.ezgg.domain.matchInfo.matchKeyword.championInfo.ChampionBasicInfo;
import com.matching.ezgg.domain.matchInfo.matchKeyword.championInfo.ChampionRole;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.analysis.Analysis;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.GlobalKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.JugKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.LanerKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.keyword.SupKeyword;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;
import com.matching.ezgg.domain.matchInfo.service.MatchInfoService;
import com.matching.ezgg.domain.memberInfo.entity.MemberInfo;
import com.matching.ezgg.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.domain.recentTwentyMatch.dto.RecentTwentyMatchDto;
import com.matching.ezgg.domain.recentTwentyMatch.entity.model.ChampionStat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentTwentyMatchBuilderService {

	private final MemberInfoService memberInfoService;
	private final MatchInfoService matchInfoService;

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

		// 라인별 keywordAnalysis에 들어갈 값 계산
		KeywordAnalysisResult keywordAnalysisResult = makeKeywordJson(result);

		// 20경기 승률 계산
		int winRate = calculateWinRate(result.wins, result.losses);

		// most 3 챔피언들로 championStat 압축
		Map<String, ChampionStat> most3ChampionStats = extractMost3ChampionStats(result.allChampionStats);

		RecentTwentyMatchDto recentTwentyMatchDto = RecentTwentyMatchDto.builder()
			.memberId(memberId)
			.sumKills(result.sumKills)
			.sumDeaths(result.sumDeaths)
			.sumAssists(result.sumAssists)
			.winRate(winRate)
			.championStats(most3ChampionStats)
			.topAnalysis(convertToJson(keywordAnalysisResult.topKeywordAnalysis))
			.jugAnalysis(convertToJson(keywordAnalysisResult.jugKeywordAnalysis))
			.midAnalysis(convertToJson(keywordAnalysisResult.midKeywordAnalysis))
			.adAnalysis(convertToJson(keywordAnalysisResult.adKeywordAnalysis))
			.supAnalysis(convertToJson(keywordAnalysisResult.supKeywordAnalysis))
			.build();

		log.info("recentTwentyMatch 계산 종료");
		return recentTwentyMatchDto;
	}

	public String convertToJson(Analysis<? extends Enum<?>> analysis) {
		ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(analysis);
			log.info("[INFO] Analysis : {}", json);
		} catch (JsonProcessingException e) {
			log.error("[ERROR] Analysis : {}", e.getMessage());
		}
		return json;
	}


	// 계산 결과값 보관하기 위한 내부 클래스
	private static class AggregateResult {
		int sumKills = 0;
		int sumDeaths = 0;
		int sumAssists = 0;
		int wins = 0;
		int losses = 0;
		Map<String, Integer> laneCount = new LinkedHashMap<>();
		Map<String, Integer> topKeywordCount = new LinkedHashMap<>();
		Map<String, Integer> jugKeywordCount = new LinkedHashMap<>();
		Map<String, Integer> midKeywordCount = new LinkedHashMap<>();
		Map<String, Integer> adKeywordCount = new LinkedHashMap<>();
		Map<String, Integer> supKeywordCount = new LinkedHashMap<>();
		Map<ChampionRole, Integer> topRoleCount = new LinkedHashMap<>();
		Map<ChampionRole, Integer> jugRoleCount = new LinkedHashMap<>();
		Map<ChampionRole, Integer> midRoleCount = new LinkedHashMap<>();
		Map<ChampionRole, Integer> adRoleCount = new LinkedHashMap<>();
		Map<ChampionRole, Integer> supRoleCount = new LinkedHashMap<>();
		Map<String, ChampionStat> allChampionStats = new HashMap<>();
	}

	// recentTwentyMatch 계산 메서드
	private AggregateResult calculateAggregateStatsFromMatches(List<String> matchIds, Long memberId) {

		log.info("Riot Api Match 반복문 시작");
		AggregateResult result = new AggregateResult();

		// 최근 20 경기의 matchId들로 RecentTwentyMatch 업데이트
		for (String matchId : matchIds) {
			MatchInfo matchInfo = matchInfoService.getMatchByMemberIdAndRiotMatchId(memberId, matchId);

			result.sumKills += matchInfo.getKills();
			result.sumDeaths += matchInfo.getDeaths();
			result.sumAssists += matchInfo.getAssists();

			if (Boolean.TRUE.equals(matchInfo.getWin())) {
				result.wins++;
			} else {
				result.losses++;
			}

			// championStat 업데이트
			result.allChampionStats
				.computeIfAbsent(matchInfo.getChampionName().trim().toLowerCase(),
					name -> ChampionStat.builder()
						.championName(name)
						.build())// 해당 챔피언이 처음으로 기록될때만 ChampionStat 객체 생성. 있을 시에는 해당 championStat 객체에 updateByMatch메서드 바로 적용
				.updateByMatch(matchInfo);

			// 라인별 KeywordAnalysis에 사용할 요소들 업데이트
			Lane lane = null;

			try {
				lane = Lane.valueOf(matchInfo.getTeamPosition());
			} catch (IllegalArgumentException | NullPointerException e) {
				throw new IllegalArgumentException("유효하지 않은 Lane명 입니다.", e);
			}

			// 라인 횟수 카운트
			result.laneCount.put(lane.name(), result.laneCount.getOrDefault(lane.name(), 0) + 1);

			// 라인별 키워드 횟수 카운트
			Map<Lane, Map<String, Integer>> laneKeywordMap = createLaneKeywordMap(result);

			//특정 라인의 키워드 카운트 맵 꺼냄
			Map<String, Integer> keywordCountMap = laneKeywordMap.get(lane);

			for (String keyword : matchInfo.getMatchKeywords()) {
				keywordCountMap.put(keyword, keywordCountMap.getOrDefault(keyword, 0) + 1);
			}

			// 라인별 챔피언 역할 횟수 카운트
			Map<Lane, Map<ChampionRole, Integer>> laneChampionRoleMap = createChampionRoleMap(result);

			//특정 라인의 챔피언 롤 카운트 맵 꺼냄
			Map<ChampionRole, Integer> championRoleCountMap = laneChampionRoleMap.get(lane);

			String championName = cleanedName(matchInfo.getChampionName());
			List<ChampionRole> championRoles = new ArrayList<>();
			try {
				championRoles = ChampionBasicInfo.valueOf(championName).getChampionRoles();
			} catch (IllegalArgumentException | NullPointerException e) {
				throw new IllegalArgumentException("유효하지 않은 챔피언명 입니다.", e);
			}

			for (ChampionRole championRole : championRoles) {
				championRoleCountMap.put(championRole, championRoleCountMap.getOrDefault(championRole, 0) + 1);
			}
		}

		return result;
	}

	/**
	 * 챔피언명에서 특수문자 및 공백 제거하는 메서드
	 * @param championName
	 * @return 챔피언명 String
	 */
	private String cleanedName(String championName) {
		return championName
			.replaceAll("[^a-zA-Z0-9]", "")
			.toUpperCase();
	}

	/**
	 * 각 라인별 KeywordAnalysis 보관용 내부 클래스
	 */

	private static class KeywordAnalysisResult {
		Analysis<LanerKeyword> topKeywordAnalysis = Analysis.<LanerKeyword>builder()
			.enumClass(LanerKeyword.class)
			.build();
		Analysis<JugKeyword> jugKeywordAnalysis = Analysis.<JugKeyword>builder()
			.enumClass(JugKeyword.class)
			.build();
		Analysis<LanerKeyword> midKeywordAnalysis = Analysis.<LanerKeyword>builder()
			.enumClass(LanerKeyword.class)
			.build();
		Analysis<LanerKeyword> adKeywordAnalysis = Analysis.<LanerKeyword>builder()
			.enumClass(LanerKeyword.class)
			.build();
		Analysis<SupKeyword> supKeywordAnalysis = Analysis.<SupKeyword>builder()
			.enumClass(SupKeyword.class)
			.build();
	}

	/**
	 * 라인과 라인별 키워드 횟수를 묶은 Map을 생성하는 메서드
	 * @param result
	 * @return Map<Lane, Map<String(키워드명), Integer(횟수)>>
	 */

	private Map<Lane, Map<String, Integer>> createLaneKeywordMap(AggregateResult result) {
		return Map.of(
			Lane.TOP, result.topKeywordCount,
			Lane.JUNGLE, result.jugKeywordCount,
			Lane.MIDDLE, result.midKeywordCount,
			Lane.BOTTOM, result.adKeywordCount,
			Lane.UTILITY, result.supKeywordCount
		);
	}

	/**
	 * 라인과 championRole 키워드 횟수를 묶은 Map을 생성하는 메서드
	 * @param result
	 * @return Map<Lane, Map<String(챔피언 역할명), Integer(횟수)>>
	 */

	private Map<Lane, Map<ChampionRole, Integer>> createChampionRoleMap(AggregateResult result) {
		return Map.of(
			Lane.TOP, result.topRoleCount,
			Lane.JUNGLE, result.jugRoleCount,
			Lane.MIDDLE, result.midRoleCount,
			Lane.BOTTOM, result.adRoleCount,
			Lane.UTILITY, result.supRoleCount
		);
	}

	/**
	 * evaluateKeyword를 통해 키워드 등급을 계산하고
	 * 해당 등급들을 Analysis 클래스를 이용해 JSON 형태로 만드는 메서드
	 * @param result
	 * @return 각 라인별 키워드 분석 Analysis가 들어있는 keywordAnalysisResult
	 */

	private KeywordAnalysisResult makeKeywordJson(AggregateResult result) {
		Map<String, Integer> laneCounts = result.laneCount;

		//기본 Analysis 생성
		KeywordAnalysisResult keywordAnalysisResult = new KeywordAnalysisResult();

		//라인과 라인별 키워드 횟수를 묶은 Map 생성
		Map<Lane, Map<String, Integer>> laneKeywordMap = createLaneKeywordMap(result);

		//라인과 챔피언 롤 횟수를 묶은 Map 생성
		Map<Lane, Map<ChampionRole, Integer>> championRoleMap = createChampionRoleMap(result);

		//laneKeywordMap 돌며 반복
		for (Map.Entry<Lane, Map<String, Integer>> entry : laneKeywordMap.entrySet()) {

			Lane lane = entry.getKey();
			Map<String, Integer> keywordCounts = entry.getValue();
			int lanePlayCount = laneCounts.getOrDefault(lane.name(), 0);

			//라인에 따라 사용할 Analysis 변경
			Analysis<? extends Enum<?>> analysis = switch (lane) {
				case TOP -> keywordAnalysisResult.topKeywordAnalysis;
				case JUNGLE -> keywordAnalysisResult.jugKeywordAnalysis;
				case MIDDLE -> keywordAnalysisResult.midKeywordAnalysis;
				case BOTTOM -> keywordAnalysisResult.adKeywordAnalysis;
				case UTILITY -> keywordAnalysisResult.supKeywordAnalysis;
			};

			//키워드 개수만큼 반복
			for (Map.Entry<String, Integer> keywordEntry : keywordCounts.entrySet()) {
				String keyword = keywordEntry.getKey();
				int count = keywordEntry.getValue();

				//Global 키워드이면 키워드 등급 계산해서 globalAnalysis 수정
				if (Arrays.stream(GlobalKeyword.values()).anyMatch(k -> k.name().equalsIgnoreCase(keyword))) {
					analysis.getGlobal().put(keyword, evaluateKeyword(count, lanePlayCount));
				} else { //아니면 LaneAnalysis 수정
					analysis.getLaner().put(keyword, evaluateKeyword(count, lanePlayCount));
				}
			}
		}

		//championRoleMap 돌며 반복
		for (Map.Entry<Lane, Map<ChampionRole, Integer>> entry : championRoleMap.entrySet()) {

			Lane lane = entry.getKey();
			Map<ChampionRole, Integer> championRoleCounts = entry.getValue();
			int lanePlayCount = laneCounts.getOrDefault(lane.name(), 0);

			//라인에 따라 사용할 Analysis 변경
			Analysis<? extends Enum<?>> analysis = switch (lane) {
				case TOP -> keywordAnalysisResult.topKeywordAnalysis;
				case JUNGLE -> keywordAnalysisResult.jugKeywordAnalysis;
				case MIDDLE -> keywordAnalysisResult.midKeywordAnalysis;
				case BOTTOM -> keywordAnalysisResult.adKeywordAnalysis;
				case UTILITY -> keywordAnalysisResult.supKeywordAnalysis;
			};

			//챔피언 롤 개수만큼 반복
			for (Map.Entry<ChampionRole, Integer> championRoleEntry : championRoleCounts.entrySet()) {
				ChampionRole championRole = championRoleEntry.getKey();
				int count = championRoleEntry.getValue();
				analysis.getChampionRole().put(championRole.name(), evaluateChampionRole(count, lanePlayCount));
			}
		}
		return keywordAnalysisResult;
	}


	/**
	 * 경기 수 대비 키워드 수로 키워드 등급 계산하는 메서드
	 * @param keywordCount
	 * @param lanePlayCount
	 * @return 키워드 등급 String
	 */

	private String evaluateKeyword(int keywordCount, int lanePlayCount) {
		if (lanePlayCount <= 3) { //해당 라인을 3회 이하로 플레이한 경우
			return (keywordCount >= 1) ? "평범" : "없음"; //평범 아니면 없음만 리턴
		}

		if (keywordCount == 0) {
			return "없음";
		}

		double ratio = (double) keywordCount / lanePlayCount;

		if (ratio >= 0.75) return "매우 좋음";
		if (ratio >= 0.5) return "좋음";
		if (ratio >= 0.25) return "평범";
		return "없음";
	}

	/**
	 * 챔피언 역할 등급을 계산하는 메서드
	 * @param championRoleCount
	 * @return 챔피언 역할 등급 String
	 */

	private String evaluateChampionRole(int championRoleCount, int lanePlayCount) {
		if (championRoleCount == 0) {
			return "없음";
		}
		if (lanePlayCount <= 3) { //해당 라인을 3회 이하로 플레이한 경우
			return "보통";
		}
		double ratio = (double) championRoleCount / lanePlayCount;
		return ratio >= 0.4 ? "좋음" : "보통";
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
				.thenComparing(Comparator.comparingDouble(ChampionStat::calculateKda)
					.reversed())
			)
			.limit(3)
			.collect(Collectors.toMap(
				ChampionStat::getChampionName, // key 값
				Function.identity(), // value 값(championStat)
				(a, b) -> a, // key 중복 발생 시 첫번째를 선택
				LinkedHashMap::new // 모스트 1,2,3 순서가 유지되도록 LinkedHashMap 사용
			));
	}
}
