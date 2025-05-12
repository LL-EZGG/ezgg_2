package com.matching.ezgg.redis.match;

import static com.matching.ezgg.redis.match.RedisKey.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.domain.matching.dto.MatchingSuccessResponse;
import com.matching.ezgg.domain.matching.dto.MemberDataBundle;
import com.matching.ezgg.domain.memberInfo.service.MemberDataBundleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

	private final StringRedisTemplate stringRedisTemplate;
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;
	private final SimpMessagingTemplate messagingTemplate;
	private final MemberDataBundleService memberDataBundleService;

	public void saveMatchRequest(MatchingFilterParsingDto matchingFilterParsingDto) {
		try {
			String memberId = String.valueOf(matchingFilterParsingDto.getMemberId());

			// 기존 데이터 확인
			String existingStreamId = (String)redisTemplate.opsForHash().get(STREAM_ID_HASH_KEY.getValue(), memberId);
			if (existingStreamId == null) {
				String json = objectMapper.writeValueAsString(matchingFilterParsingDto);
				Map<String, String> message = new HashMap<>();
				message.put("data", json);

				RecordId recordId = redisTemplate.opsForStream().add(STREAM_KEY.getValue(), message);
				redisTemplate.opsForHash().put(STREAM_ID_HASH_KEY.getValue(), memberId, recordId.getValue());

				log.info("Redis Stream 및 es 매칭 요청 저장 완료 : {}", matchingFilterParsingDto.getMemberId());
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Stream 등록 실패", e);
		}
	}

	private boolean isDataEqual(MatchingFilterParsingDto dto1, MatchingFilterParsingDto dto2) {
		// 필수 필드만 비교하여 데이터 변경 여부 확인
		return dto1.getMemberId().equals(dto2.getMemberId()) &&
			dto1.getPreferredPartnerParsing()
				.getWantLine()
				.getMyLine()
				.equals(dto2.getPreferredPartnerParsing().getWantLine().getMyLine()) &&
			dto1.getPreferredPartnerParsing()
				.getWantLine()
				.getPartnerLine()
				.equals(dto2.getPreferredPartnerParsing().getWantLine().getPartnerLine()) &&
			dto1.getMemberInfoParsing().getTier().equals(dto2.getMemberInfoParsing().getTier());
	}

	public void acknowledgeMatch(Long memberId) {
		try {
			String memberIdStr = String.valueOf(memberId);
			String streamId = (String)redisTemplate.opsForHash().get(STREAM_ID_HASH_KEY.getValue(), memberIdStr);

			if (streamId != null) {
				redisTemplate.opsForStream().acknowledge(STREAM_KEY.getValue(), STREAM_GROUP.getValue(), streamId);
				redisTemplate.opsForStream().delete(STREAM_KEY.getValue(), streamId);
				redisTemplate.opsForHash().delete(STREAM_ID_HASH_KEY.getValue(), memberIdStr);
			}
		} catch (Exception e) {
			log.error("Stream 처리 중 에러 발생 : {}", e.getMessage());
		}
	}

	private MatchingSuccessResponse getMatchingSuccessResponse(Long matchedMemberId) {
		MemberDataBundle data = memberDataBundleService.getMemberDataBundleByMemberId(matchedMemberId);

		return MatchingSuccessResponse.builder()
			.status("SUCCESS")
			.data(MatchingSuccessResponse.MatchedMemberData.builder()
				.matchedMemberId(matchedMemberId)
				.memberInfoDto(data.getMemberInfoDto())
				.recentTwentyMatchDto(data.getRecentTwentyMatchDto())
				.build())
			.build();
	}

	public void sendMatchingSuccessResponse(Long memberId, Long matchedMemberId) {
		messagingTemplate.convertAndSendToUser(memberId.toString(), "/queue/matching",
			getMatchingSuccessResponse(matchedMemberId));
	}

	public void retryMatchRequest(MatchingFilterParsingDto matchingFilterParsingDto) {
		try {
			String memberId = String.valueOf(matchingFilterParsingDto.getMemberId());

			// 기존 retry 데이터 확인
			Set<String> retryCandidates = getRetryCandidates();
			if (retryCandidates != null) {
				for (String json : retryCandidates) {
					try {
						MatchingFilterParsingDto existingDto = objectMapper.readValue(json,
							MatchingFilterParsingDto.class);
						if (existingDto.getMemberId().equals(matchingFilterParsingDto.getMemberId())) {
							// 동일한 유저의 데이터가 있는 경우, 변경 여부 확인
							if (isDataEqual(existingDto, matchingFilterParsingDto)) {
								log.info("동일한 매칭 요청이 이미 retry 큐에 존재합니다. 업데이트하지 않습니다. : {}", memberId);
								return;
							}

							// 데이터가 다른 경우 기존 데이터 삭제
							removeRetryCandidate(json);
							log.info("기존 retry 데이터 삭제 완료 : {}", memberId);
							break;
						}
					} catch (JsonProcessingException e) {
						log.error("Retry 큐 데이터 파싱 실패 : {}", e.getMessage());
					}
				}
			}

			// 새 retry 데이터 저장
			String json = objectMapper.writeValueAsString(matchingFilterParsingDto);
			long retryTime = System.currentTimeMillis() + 10000; // 10초 후 재시도
			long delay = retryTime - System.currentTimeMillis(); // 실제 남은 시간 계산

			redisTemplate.opsForZSet().add(RETRY_ZSET_KEY.getValue(), json, retryTime);
			log.info("딜레이 큐에 등록 완료 ({}초 후 재시도 예정) : {}", delay / 1000, memberId);
		} catch (JsonProcessingException e) {
			log.error("딜레이 큐 등록 실패 : {}", e.getMessage());
		}
	}

	public void createStringGroup() {
		stringRedisTemplate.opsForStream()
			.createGroup(STREAM_KEY.getValue(), ReadOffset.latest(), STREAM_GROUP.getValue());
	}

	public List<MapRecord<String, Object, Object>> getStringGroup() {
		return stringRedisTemplate.opsForStream().read(
			Consumer.from(STREAM_GROUP.getValue(), CONSUMER_NAME.getValue()),
			StreamReadOptions.empty().count(5).block(Duration.ofMillis(2000)),
			StreamOffset.create(STREAM_KEY.getValue(), ReadOffset.lastConsumed()));
	}

	public Set<String> getRetryCandidates() {
		long now = System.currentTimeMillis();
		return redisTemplate.opsForZSet().rangeByScore(RETRY_ZSET_KEY.getValue(), 0, now);
	}

	public void removeRetryCandidate(String json) {
		redisTemplate.opsForZSet().remove(RETRY_ZSET_KEY.getValue(), json);
	}
}
