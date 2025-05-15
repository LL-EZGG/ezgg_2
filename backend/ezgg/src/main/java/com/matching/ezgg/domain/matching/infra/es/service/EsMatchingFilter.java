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
		List<String> preferredChampion, List<String> unpreferredChampion) {
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

	private MatchingFilterParsingDto updateUserMatchingScore(MatchingFilterParsingDto user,
		List<String> preferredChampions,
		List<String> unpreferredChampions) {

		List<RecentTwentyMatchParsingDto.MostChampion> mostChampionList = user.getRecentTwentyMatchParsing()
			.getMostChampions();

		int preferredChampionWeight = 0;
		int unpreferredChampionWeight = 0;

		// 선호 챔피언 처리
		if (preferredChampions != null && !preferredChampions.isEmpty()) {
			for (String champion : preferredChampions) {
				if (champion != null && !champion.isEmpty()) {
					// 선호 챔피언이 상대방의 모스트 챔피언 목록에 있는지 확인
					RecentTwentyMatchParsingDto.MostChampion championInfo =
						findChampionInMostList(champion, mostChampionList);

					if (championInfo != null) {
						// 선호 챔피언이 모스트에 있으면 가중치 계산
						preferredChampionWeight += getChampionWeight(champion, true, mostChampionList);
						log.debug("선호 챔피언 {} 가중치: {}", champion,
							getChampionWeight(champion, true, mostChampionList));
					} else {
						// 여기서 조건에 따라 처리를 변경할 수 있습니다.
						// 현재는 선호 챔피언이 상대방의 모스트에 없어도 계속 진행합니다.
						log.debug("선호 챔피언 {}가 상대방의 모스트 챔피언 목록에 없습니다.", champion);
					}
				}
			}
		}

		// 비선호 챔피언 처리
		if (unpreferredChampions != null && !unpreferredChampions.isEmpty()) {
			for (String champion : unpreferredChampions) {
				if (champion != null && !champion.isEmpty()) {
					// 비선호 챔피언이 상대방의 모스트 챔피언 목록에 있는지 확인
					RecentTwentyMatchParsingDto.MostChampion championInfo =
						findChampionInMostList(champion, mostChampionList);

					if (championInfo != null) {
						// 비선호 챔피언이 모스트에 있으면 가중치 계산
						unpreferredChampionWeight += getChampionWeight(champion, false, mostChampionList);
						log.debug("비선호 챔피언 {} 가중치: {}", champion,
							getChampionWeight(champion, false, mostChampionList));
					} else {
						log.debug("비선호 챔피언 {}가 상대방의 모스트 챔피언 목록에 없습니다.", champion);
					}
				}
			}
		}

		// 최종 매칭 점수 계산: 선호 챔피언 가중치 - 비선호 챔피언 가중치
		int matchingScore = preferredChampionWeight - unpreferredChampionWeight;
		log.debug("User ID: {}, 최종 매칭 점수: {}", user.getMemberId(), matchingScore);

		// 매칭 점수가 음수인 경우 0으로 설정 (선택적)
		// matchingScore = Math.max(0, matchingScore);

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
		// int winRateWeight = championInfo.getWinRateOfChampion() / 10; // 승률을 10으로 나눠 0~10 범위의 가중치로 변환
		int winRateWeight = 0; // 승률관련 가중치 정해진게 없어 0으로 처리

		// 순위 가중치
		int rankWeight;
		switch (championRank) {
			case 1:
				rankWeight = 5;
				break; // 1위는 높은 가중치
			case 2:
				rankWeight = 3;
				break; // 2위는 중간 가중치
			case 3:
				rankWeight = 1;
				break; // 3위는 낮은 가중치
			default:
				rankWeight = 0; // 나머지는 가중치 없음
		}

		// 선호/비선호 여부에 상관없이 동일한 가중치 적용
		// (이 부분은 비즈니스 로직에 따라 다르게 처리할 수 있음)
		return rankWeight + winRateWeight;
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
