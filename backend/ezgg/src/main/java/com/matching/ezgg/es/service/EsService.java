package com.matching.ezgg.es.service;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.matching.ezgg.global.exception.EsPostException;
import com.matching.ezgg.matching.dto.MatchingFilterDto;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class EsService {

	private final ElasticsearchClient esClient;

	public void esPost(MatchingFilterDto matchingFilterDto) {
		IndexRequest<MatchingFilterDto> postRequest = IndexRequest.of(r -> r
			.index("matching-user")
			.document(matchingFilterDto));
		try {
			IndexResponse response = esClient.index(postRequest);
			log.info("ES 저장: {}", response);
		} catch (IOException e) {
			throw new EsPostException();
		}
	}

}
