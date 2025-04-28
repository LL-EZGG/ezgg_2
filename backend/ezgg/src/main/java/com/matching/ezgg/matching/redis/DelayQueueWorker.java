package com.matching.ezgg.matching.redis;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.matching.dto.MatchingFilterParsingDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DelayQueueWorker {

	private final RedisTemplate<String, String> redisTemplate;
	private final RedisStreamProducer redisStreamProducer;
	private final ObjectMapper objectMapper;

	private static final String RETRY_ZSET_KEY = "matching-retry-zset";

	@Scheduled(fixedDelay = 3000) // 3초마다 실행
	public void processRetryQueue() {
		long now = System.currentTimeMillis();

		Set<String> retryCandidates = redisTemplate.opsForZSet().rangeByScore(RETRY_ZSET_KEY, 0, now);

		if(retryCandidates == null || retryCandidates.isEmpty()) {
			return;
		}

		for(String json : retryCandidates) {
			try {
				MatchingFilterParsingDto dto = objectMapper.readValue(json, MatchingFilterParsingDto.class);
				redisStreamProducer.sendMatchRequest(dto);
				redisTemplate.opsForZSet().remove(RETRY_ZSET_KEY, json);
				log.info("Retry 처리 완료 : {}", json);
			} catch (Exception e) {
				log.error("Retry 처리 중 에러 발생 : {}", e.getMessage());
			}
		}
	}
}
