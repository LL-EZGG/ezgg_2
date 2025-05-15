package com.matching.ezgg.domain.matching.infra.es.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.domain.matching.dto.RecentTwentyMatchParsingDto;
import com.matching.ezgg.global.exception.EsQueryException;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class EsMatchingFilter {
	private final ElasticsearchClient esClient;

	public List<MatchingFilterParsingDto> findMatchingUsers(String myLine, String partnerLine, String tier,
		Long myMemberId,
		String preferredChampion, String unpreferredChampion) {
		Query query = Query.of(q -> q
			.bool(b -> b
				.filter(
					Query.of(q1 -> q1.match(m -> m
						.field("memberInfoParsing.tier.keyword")
						.query(tier)
					)),
					Query.of(q2 -> q2.match(m -> m
						.field("preferredPartnerParsing.wantLine.partnerLine.keyword")
						.query(myLine)
					)),
					Query.of(q3 -> q3.match(m -> m
						.field("preferredPartnerParsing.wantLine.myLine.keyword")
						.query(partnerLine)
					))
				)
				.mustNot(mn -> mn
					.term(t -> t
						.field("memberId")
						.value(myMemberId)
					)
				)
			)
		);

		SearchRequest searchRequest = SearchRequest.of(s -> s
			.index("matching-user")
			.query(query)
		);

		try {
			SearchResponse<MatchingFilterParsingDto> response = esClient.search(searchRequest,
				MatchingFilterParsingDto.class);

			return response.hits().hits().stream()
				.map(Hit::source)
				.filter(Objects::nonNull)
				.map(user -> updateUserMatchingScore(user, preferredChampion, unpreferredChampion))
				.sorted(Comparator.comparing(MatchingFilterParsingDto::getMatchingScore).reversed())
				.collect(Collectors.toList());

		} catch (IOException e) {
			log.error("Elasticsearch 조건 조회 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
			throw new EsQueryException();
		}
	}

	// 가중치 계산 로직
	private MatchingFilterParsingDto updateUserMatchingScore(MatchingFilterParsingDto user, String preferredChampion,
		String unpreferredChampion) {
		List<RecentTwentyMatchParsingDto.MostChampion> mostChampionList = user.getRecentTwentyMatchParsing()
			.getMostChampions();

		// 내가 선호하는 챔피언과 비선호하는 챔피언이 상대방의 모스트 챔피언 목록에 있는지 확인
		int preferredChampionWeight = getChampionWeight(preferredChampion, true, mostChampionList);
		int unpreferredChampionWeight = getChampionWeight(unpreferredChampion, false, mostChampionList);

		// 최종 매칭 점수 계산: 선호 챔피언 가중치 - 비선호 챔피언 가중치
		// 음수가 나올수 잇음 ( 비선호 챔피언에 가중치가 더 높을 경우 )
		int matchingScore = preferredChampionWeight - unpreferredChampionWeight;

		return user.toBuilder()
			.matchingScore(matchingScore)
			.build();
	}

	public int getChampionWeight(String championName, boolean isPreferred,
		List<RecentTwentyMatchParsingDto.MostChampion> mostChampions) {

		// 해당 챔피언의 정보 찾기
		RecentTwentyMatchParsingDto.MostChampion championInfo = findChampionInMostList(championName, mostChampions);

		// 챔피언이 모스트 목록에 없으면 0 반환
		if (championInfo == null) {
			return 0;
		}

		// 챔피언 순위 확인
		int championRank = getChampionRank(championName, mostChampions);

		// 승률 가중치 (승률이 높을수록 가중치 증가)
		int winRateWeight = championInfo.getWinRateOfChampion() / 10; // 승률을 10으로 나눠 0~10 범위의 가중치로 변환

		// 순위 가중치
		int rankWeight = 0;
		if (championRank == 1) {
			rankWeight = 5; // 1위는 높은 가중치
		} else if (championRank == 2) {
			rankWeight = 3; // 2위는 중간 가중치
		} else if (championRank == 3) {
			rankWeight = 1; // 3위는 낮은 가중치
		}

		// 최종 가중치 계산
		if (isPreferred) {
			// 선호 챔피언인 경우: 순위와 승률 모두 고려하여 가중치 증가
			return rankWeight + winRateWeight;
		} else {
			// 비선호 챔피언인 경우: 순위와 승률 모두 고려하여 부정적 가중치 부여
			return rankWeight + winRateWeight;
		}
	}

	private RecentTwentyMatchParsingDto.MostChampion findChampionInMostList(String championName,
		List<RecentTwentyMatchParsingDto.MostChampion> mostChampions) {

		if (championName == null || championName.isEmpty()) {
			return null;
		}

		return mostChampions.stream()
			.filter(champion -> champion.getChampionName().equals(championName))
			.findFirst()
			.orElse(null);
	}

	public int getChampionRank(String championName, List<RecentTwentyMatchParsingDto.MostChampion> mostChampions) {
		// 챔피언이 모스트 챔피언 목록에서 몇 번째 순위인지 확인
		log.debug("Finding rank for: {}", championName);
		for (int i = 0; i < mostChampions.size(); i++) {
			log.debug("Candidate: {}", mostChampions.get(i).getChampionName());
			if (mostChampions.get(i).getChampionName().equals(championName)) {
				return i + 1; // 1위부터 시작하므로 i + 1을 반환
			}
		}
		return Integer.MAX_VALUE; // 목록에 없으면 매우 낮은 순위로 처리
	}
}
