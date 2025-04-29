package com.matching.ezgg.redis.match;

import org.springframework.stereotype.Component;

import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamProducer {

	private final RedisService redisService;

	public void sendMatchRequest(MatchingFilterParsingDto matchingFilterParsingDto) {
		redisService.saveMatchRequest(matchingFilterParsingDto);
	}

	public void acknowledgeBothUser(Long memberId1, Long memberId2) {
		redisService.acknowledgeMatch(memberId1, memberId2);
	}

	public void retryLater(MatchingFilterParsingDto matchingFilterParsingDto) {
		redisService.retryMatchRequest(matchingFilterParsingDto);
	}
}
