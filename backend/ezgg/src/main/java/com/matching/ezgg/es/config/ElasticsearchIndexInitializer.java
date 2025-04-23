package com.matching.ezgg.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

import com.matching.ezgg.es.index.MatchingUserES;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer implements ApplicationListener<ContextClosedEvent> {

	private final ElasticsearchOperations elasticsearchOperations;

	@PostConstruct
	public void createIndex() {
		IndexOperations indexOps = elasticsearchOperations.indexOps(MatchingUserES.class);
		if (!indexOps.exists()) {
			indexOps.create();
			indexOps.putMapping(indexOps.createMapping());
			log.info("Elasticsearch 인덱스 생성 완료");
		}
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		IndexOperations indexOps = elasticsearchOperations.indexOps(MatchingUserES.class);
		if (indexOps.exists()) {
			indexOps.delete();
			log.info("Elasticsearch 인덱스 삭제 완료");
		}
	}

	@Bean("es")
	public static ElasticsearchClient createClient() {
		RestClient restClient = RestClient.builder(
			new HttpHost("localhost", 9200)
		).build();

		RestClientTransport transport = new RestClientTransport(
			restClient, new JacksonJsonpMapper()
		);

		return new ElasticsearchClient(transport);
	}
}
