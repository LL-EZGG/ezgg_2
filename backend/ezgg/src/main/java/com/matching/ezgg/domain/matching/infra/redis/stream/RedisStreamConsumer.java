package com.matching.ezgg.domain.matching.infra.redis.stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.domain.matching.infra.redis.service.RedisService;
import com.matching.ezgg.domain.matching.service.MatchingProcessor;
import com.matching.ezgg.domain.review.service.ReviewService;

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
	private final ReviewService reviewService;

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
				return;
			}

			log.info("queue 개수 : {} ", messages.size());

			for (MapRecord<String, Object, Object> message : messages) {
				String json = (String)message.getValue().get("data");
				MatchingFilterParsingDto dto = objectMapper.readValue(json, MatchingFilterParsingDto.class);

				if (redisService.isInDeleteQueue(dto.getMemberId())) {
					log.info("is in delete queue : {} ", dto.getMemberId());
					return;
				}
				matchingProcessor.tryMatch(dto);
			}

		} catch (Exception e) {
			log.error("Redis Stream Consumer 처리 중 에러 발생 : {}", e.getMessage());
		}
	}

	@Scheduled(fixedDelay = 1000 * 60) // 1분마다 실행
	public void findDuoGame() {
		List<Map<String, String>> matchedUsers = redisService.getTwentyMatchedUsers();
		if (matchedUsers == null || matchedUsers.isEmpty()) {
			return;
		}

		for (Map<String, String> matchedUser : matchedUsers) {
			Long memberId1 = Long.valueOf(matchedUser.get("memberId1"));
			Long memberId2 = Long.valueOf(matchedUser.get("memberId2"));

			// 3회 카운트 초과 시 매칭된 유저 삭제
			if(!redisService.canExecuteReview(memberId1, memberId2)){
				redisService.deleteMatchedUser(matchedUser);
				continue;
			}

			Map<String, String> updateMatchedUser = new HashMap<>();
			updateMatchedUser.put("memberId1", matchedUser.get("memberId1"));
			updateMatchedUser.put("memberId2", matchedUser.get("memberId2"));
			updateMatchedUser.put("timestamp", String.valueOf(System.currentTimeMillis()));

			try {
				redisService.updateMatchedUser(matchedUser, updateMatchedUser);
			}catch (Exception e) {
				log.error("Redis에 매칭된 유저 업데이트 실패: {}", e.getMessage());
			}

			reviewService.findDuoGame(memberId1, memberId2, updateMatchedUser);
		}
	}
}


