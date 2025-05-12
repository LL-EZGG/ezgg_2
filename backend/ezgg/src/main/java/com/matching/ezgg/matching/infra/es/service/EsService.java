package com.matching.ezgg.matching.infra.es.service;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.matching.ezgg.global.exception.EsDocDeleteFailException;
import com.matching.ezgg.global.exception.EsPostFailException;
import com.matching.ezgg.matching.dto.MatchingFilterParsingDto;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
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
			.id(String.valueOf(matchingFilterDto.getMemberId()))
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
			DeleteRequest deleteRequest = DeleteRequest.of(d -> d
				.index(indexName)
				.id(String.valueOf(memberId)) // memberId를 직접 문서 ID로 사용
			);
			DeleteResponse response = esClient.delete(deleteRequest);
			log.info("Doc 삭제 완료: {}", response);
		} catch (IOException e) {
			log.error("Elasticsearch Doc 삭제 중 IOException 발생", e);
			throw new EsDocDeleteFailException();
		}
	}
}
