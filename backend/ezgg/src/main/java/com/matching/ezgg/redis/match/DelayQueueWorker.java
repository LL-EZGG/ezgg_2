package com.matching.ezgg.redis.match;

import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DelayQueueWorker {

	private final RedisService redisService;
	private final RedisStreamProducer redisStreamProducer;
	private final ObjectMapper objectMapper;

	@Scheduled(fixedDelay = 3000) // 3초마다 실행
	public void processRetryQueue() {
		Set<String> retryCandidates = redisService.getRetryCandidates();

		if (retryCandidates == null || retryCandidates.isEmpty()) {
			return;
		}

		for (String json : retryCandidates) {
			try {
				MatchingFilterParsingDto dto = objectMapper.readValue(json, MatchingFilterParsingDto.class);
				redisStreamProducer.sendMatchRequest(dto);
				redisService.removeRetryCandidate(json);
				log.info("Retry 처리 완료 : {}", json);
			} catch (Exception e) {
				log.error("Retry 처리 중 에러 발생 : {}", e.getMessage());
			}
		}
	}
}
