package com.matching.ezgg.matching.redis;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.matching.dto.MatchingFilterParsingDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamProducer {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	private static final String STREAM_KEY = "matching-stream";
	private static final String STREAM_ID_HASH_KEY = "stream-id-hash";
	private static final String STREAM_GROUP = "matching-group";
	private static final String RETRY_ZSET_KEY = "matching-retry-zset";

	// 매칭 요청을 Redis Stream에 저장하는 메서드
	public void sendMatchRequest(MatchingFilterParsingDto matchingFilterParsingDto) {
		try {
			String json = objectMapper.writeValueAsString(matchingFilterParsingDto);

			Map<String, String> message = new HashMap<>();
			message.put("data", json);

			RecordId recordId = redisTemplate.opsForStream().add(STREAM_KEY, message);

			String memberId = String.valueOf(matchingFilterParsingDto.getMemberId());
			redisTemplate.opsForHash().put(STREAM_ID_HASH_KEY, memberId, recordId.getValue());

			log.info("Redis Stream 매칭 요청 저장 완료 : {}", matchingFilterParsingDto.getMemberId());
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Stream 등록 실패", e);
		}
	}

	// 매칭 된 유저 삭제하는 매서드
	public void acknowledgeBothUser(Long memberId1, Long memberId2) {
		try {
			String memberIdStr1 = String.valueOf(memberId1);
			String memberIdStr2 = String.valueOf(memberId2);

			String streamId1 = (String) redisTemplate.opsForHash().get(STREAM_ID_HASH_KEY, memberIdStr1);
			String streamId2 = (String) redisTemplate.opsForHash().get(STREAM_ID_HASH_KEY, memberIdStr2);

			if (streamId1 != null) {
				// 스트림에서 메시지 acknowledge 처리
				redisTemplate.opsForStream().acknowledge(STREAM_KEY, STREAM_GROUP, streamId1);
				// 스트림에서 메시지 직접 삭제
				redisTemplate.opsForStream().delete(STREAM_KEY, streamId1);
				// 해시에서 매핑 삭제
				redisTemplate.opsForHash().delete(STREAM_ID_HASH_KEY, memberIdStr1);
				log.info("요청 유저 Stream 처리 완료 (ACK & 삭제) : {}", memberIdStr1);
			}
			if (streamId2 != null) {
				// 스트림에서 메시지 acknowledge 처리
				redisTemplate.opsForStream().acknowledge(STREAM_KEY, STREAM_GROUP, streamId2);
				// 스트림에서 메시지 직접 삭제
				redisTemplate.opsForStream().delete(STREAM_KEY, streamId2);
				// 해시에서 매핑 삭제
				redisTemplate.opsForHash().delete(STREAM_ID_HASH_KEY, memberIdStr2);
				log.info("상대 유저 Stream 처리 완료 (ACK & 삭제) : {}", memberIdStr2);
			}
		} catch (Exception e) {
			log.error("Stream 처리 중 에러 발생 : {}", e.getMessage());
		}
	}


	// 매칭 실피 시 재시도(zset에 등록)
	public void retryLater(MatchingFilterParsingDto matchingFilterParsingDto) {
		try {
			String memberId = String.valueOf(matchingFilterParsingDto.getMemberId());
			String json = objectMapper.writeValueAsString(matchingFilterParsingDto);

			long retryTime = System.currentTimeMillis() + 10000; // 10초 후 재시도

			redisTemplate.opsForZSet().add(RETRY_ZSET_KEY, json, retryTime);
			log.info("딜레이 큐에 등록 완료 ({}초 후 재시도 예정) : {}", retryTime/1000, memberId);
		} catch (JsonProcessingException e) {
			log.error("딜레이 큐 등록 실패 : {}", e.getMessage());
		}
	}
}
