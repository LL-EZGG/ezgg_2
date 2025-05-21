package com.matching.ezgg.domain.matching.infra.redis.stream;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.domain.matching.infra.redis.service.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamProducer {

	private final RedisService redisService;
	private final ObjectMapper objectMapper;

	public void sendMatchRequest(MatchingFilterParsingDto matchingFilterParsingDto) {
		Long memberId = matchingFilterParsingDto.getMemberId();
		if (redisService.isInDeleteQueue(memberId)) {
			redisService.deleteMemberToDeleteQueue(memberId);
		}
		redisService.saveMatchRequest(matchingFilterParsingDto);
	}

	public void acknowledgeUser(Long memberId) {
		redisService.acknowledgeMatch(memberId);
	}

	public void acknowledgeBothUser(MatchingFilterParsingDto member1, MatchingFilterParsingDto member2) {
		redisService.acknowledgeMatch(member1.getMemberId());
		redisService.acknowledgeMatch(member2.getMemberId());
		redisService.sendMatchingSuccessResponse(member1.getMemberId(), member2.getMemberId());
		redisService.sendMatchingSuccessResponse(member2.getMemberId(), member1.getMemberId());
	}

	public void retryLater(MatchingFilterParsingDto matchingFilterParsingDto) {
		redisService.retryMatchRequest(matchingFilterParsingDto);
	}

	public void removeRetryCandidate(MatchingFilterParsingDto matchingFilterParsingDto) throws JsonProcessingException {
		redisService.removeRetryCandidate(objectMapper.writeValueAsString(matchingFilterParsingDto));
	}

	public void removeAllRedisKeysByMemberId(Long memberId) {
		redisService.removeMemberFromAllRedisKeys(memberId);
	}
}
