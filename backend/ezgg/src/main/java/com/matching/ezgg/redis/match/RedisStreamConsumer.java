package com.matching.ezgg.redis.match;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
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

	private final StringRedisTemplate stringRedisTemplate;
	private final MatchingProcessor matchingProcessor;
	private final ObjectMapper objectMapper;

	private static final String STREAM_KEY = "matching-stream";
	private static final String GROUP_NAME = "matching-group";
	private static final String CONSUMER_NAME = "matching-consumer";

	@PostConstruct
	public void init() {
		try {
			stringRedisTemplate.opsForStream().createGroup(STREAM_KEY, ReadOffset.latest(), GROUP_NAME);
			log.info("Redis Stream Consumer Group 생성 완료");
		} catch (Exception e) {
			log.info("이미 Consumer Group이 존재합니다.");
		}
	}

	@Scheduled(fixedDelay = 1000) // 1초마다 실행
	public void consumeStreamMessage() {
		try {
			List<MapRecord<String, Object, Object>> messages = stringRedisTemplate.opsForStream().read(
				Consumer.from(GROUP_NAME, CONSUMER_NAME),
				StreamReadOptions.empty().count(5).block(Duration.ofMillis(2000)),
				StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()));

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


