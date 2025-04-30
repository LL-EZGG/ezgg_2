package com.matching.ezgg.redis.match;

import java.util.List;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.domain.matching.service.MatchingProcessor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConsumer {

	private final RedisService redisService;
	private final MatchingProcessor matchingProcessor;
	private final ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		try {
			redisService.createStringGroup();
			log.info("Redis Stream Consumer Group 생성 완료");
		} catch (Exception e) {
			log.info("이미 Consumer Group이 존재합니다.");
		}
	}

	@Scheduled(fixedDelay = 1000) // 1초마다 실행
	public void consumeStreamMessage() {
		try {
			List<MapRecord<String, Object, Object>> messages = redisService.getStringGroup();

			if (messages == null || messages.isEmpty()) {
				log.info("Redis Stream에서 읽은 메시지가 없습니다.");
				return;
			}

			for (MapRecord<String, Object, Object> message : messages) {
				String json = (String)message.getValue().get("data");
				MatchingFilterParsingDto dto = objectMapper.readValue(json, MatchingFilterParsingDto.class);

				matchingProcessor.tryMatch(dto);
			}

		} catch (Exception e) {
			log.error("Redis Stream Consumer 처리 중 에러 발생 : {}", e.getMessage());
		}
	}
}


