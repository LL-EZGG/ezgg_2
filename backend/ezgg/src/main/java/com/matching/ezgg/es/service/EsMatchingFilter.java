package com.matching.ezgg.es.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.matching.ezgg.global.exception.EsQueryException;
import com.matching.ezgg.matching.dto.MatchingFilterDto;

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

	public List<MatchingFilterDto> findMatchingUsers(String myLine, String partnerLine, String tier, Long myMemberId) {
		Query query = Query.of(q -> q
			.bool(b -> b
				.filter(
					Query.of(q1 -> q1.match(m -> m
						.field("memberInfo.tier.keyword")
						.query(tier)
					)),
					Query.of(q2 -> q2.match(m -> m
						.field("preferredPartner.wantLine.partnerLine.keyword")
						.query(myLine)
					)),
					Query.of(q3 -> q3.match(m -> m
						.field("preferredPartner.wantLine.myLine.keyword")
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
			SearchResponse<MatchingFilterDto> response = esClient.search(searchRequest, MatchingFilterDto.class);
			return response.hits().hits().stream()
				.map(Hit::source)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		} catch (IOException e) {
			log.error("Elasticsearch 조건 조회 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
			throw new EsQueryException();
		}
	}
}
