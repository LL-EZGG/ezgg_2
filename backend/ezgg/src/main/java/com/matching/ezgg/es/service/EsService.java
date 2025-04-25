package com.matching.ezgg.es.service;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.matching.ezgg.global.exception.EsDocDeleteFailException;
import com.matching.ezgg.global.exception.EsPostFailException;
import com.matching.ezgg.matching.dto.MatchingFilterParsingDto;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class EsService {

	private final ElasticsearchClient esClient;
	private final static String indexName = "matching-user";

	public void esPost(MatchingFilterParsingDto matchingFilterDto) {
		IndexRequest<MatchingFilterParsingDto> postRequest = IndexRequest.of(r -> r
			.index(indexName)
			.document(matchingFilterDto));
		try {
			IndexResponse response = esClient.index(postRequest);
			log.info("ES 저장: {}", response);
		} catch (IOException e) {
			log.error("Elasticsearch Doc 저장 중 IOException 발생");
			throw new EsPostFailException();
		}
	}

	public void deleteDocByMemberId(Long memberId) {
		try {
			DeleteByQueryRequest deleteByQueryRequest = DeleteByQueryRequest.of(d -> d
				.index(indexName)
				.query(q -> q
					.term(t -> t
						.field("memberId")
						.value(v -> v.longValue(memberId))
					)
				));
			DeleteByQueryResponse deleteByQueryResponse = esClient.deleteByQuery(deleteByQueryRequest);
			log.info("Doc 삭제: {}", deleteByQueryResponse);
		} catch (IOException e) {
			log.error("Elasticsearch Doc 삭제 중 IOException 발생", e);
			throw new EsDocDeleteFailException();
		}
	}
}
