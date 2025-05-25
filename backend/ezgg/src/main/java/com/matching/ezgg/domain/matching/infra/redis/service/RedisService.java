package com.matching.ezgg.domain.matching.infra.redis.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
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
import com.matching.ezgg.domain.matching.dto.MemberDataBundleDto;
import com.matching.ezgg.domain.matching.infra.redis.key.RedisKey;
import com.matching.ezgg.domain.matching.service.MemberDataBundleService;

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
			String existingStreamId = (String)redisTemplate.opsForHash()
				.get(RedisKey.STREAM_ID_HASH_KEY.getValue(), memberId);
			if (existingStreamId == null) {
				String json = objectMapper.writeValueAsString(matchingFilterParsingDto);
				Map<String, String> message = new HashMap<>();
				message.put("data", json);

				RecordId recordId = redisTemplate.opsForStream().add(RedisKey.STREAM_KEY.getValue(), message);
				redisTemplate.opsForHash().put(RedisKey.STREAM_ID_HASH_KEY.getValue(), memberId, recordId.getValue());

				log.info("[INFO] Redis Stream 및 es 매칭 요청 저장 완료 : {}", matchingFilterParsingDto.getMemberId());
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
			String streamId = (String)redisTemplate.opsForHash()
				.get(RedisKey.STREAM_ID_HASH_KEY.getValue(), memberIdStr);

			if (streamId != null) {
				redisTemplate.opsForStream()
					.acknowledge(RedisKey.STREAM_KEY.getValue(), RedisKey.STREAM_GROUP.getValue(), streamId);
				redisTemplate.opsForStream().delete(RedisKey.STREAM_KEY.getValue(), streamId);
				redisTemplate.opsForHash().delete(RedisKey.STREAM_ID_HASH_KEY.getValue(), memberIdStr);
			}
		} catch (Exception e) {
			log.error("[ERROR] Stream 처리 중 에러 발생 : {}", e.getMessage());
		}
	}

	private MatchingSuccessResponse getMatchingSuccessResponse(Long matchedMemberId, String chattingRoomId) {
		MemberDataBundleDto data = memberDataBundleService.getMemberDataBundleByMemberId(matchedMemberId);

		log.info("[INFO] 매칭 성공! >>>>> : {}, 매칭된 방번호 : {}", matchedMemberId, chattingRoomId);

		return MatchingSuccessResponse.builder()
			.status("SUCCESS")
			.data(MatchingSuccessResponse.MatchedMemberData.builder()
				.matchedMemberId(matchedMemberId)
				.memberInfoDto(data.getMemberInfoDto())
				.chattingRoomId(chattingRoomId)
				.recentTwentyMatchDto(data.getRecentTwentyMatchDto())
				.build())
			.build();
	}

	public void sendMatchingSuccessResponse(Long memberId, Long matchedMemberId, String chattingRoomId) {
		messagingTemplate.convertAndSendToUser(memberId.toString(), "/queue/matching",
			getMatchingSuccessResponse(matchedMemberId, chattingRoomId));
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
								log.info("[INFO] 동일한 매칭 요청이 이미 retry 큐에 존재합니다. 업데이트하지 않습니다. : {}", memberId);
								return;
							}

							// 데이터가 다른 경우 기존 데이터 삭제
							removeRetryCandidate(json);
							log.info("[INFO] 기존 retry 데이터 삭제 완료 : {}", memberId);
							break;
						}
					} catch (JsonProcessingException e) {
						log.error("[ERROR] Retry 큐 데이터 파싱 실패 : {}", e.getMessage());
					}
				}
			}

			// 새 retry 데이터 저장
			String json = objectMapper.writeValueAsString(matchingFilterParsingDto);
			long retryTime = System.currentTimeMillis() + 10000; // 10초 후 재시도
			long delay = retryTime - System.currentTimeMillis(); // 실제 남은 시간 계산

			redisTemplate.opsForZSet().add(RedisKey.RETRY_ZSET_KEY.getValue(), json, retryTime);
			log.info("[INFO] 딜레이 큐에 등록 완료 ({}초 후 재시도 예정) : {}", delay / 1000, memberId);
		} catch (JsonProcessingException e) {
			log.error("[INFO] 딜레이 큐 등록 실패 : {}", e.getMessage());
		}
	}

	public void createStringGroup() {
		stringRedisTemplate.opsForStream().createGroup(
			RedisKey.STREAM_KEY.getValue(), ReadOffset.latest(), RedisKey.STREAM_GROUP.getValue());
	}

	public List<MapRecord<String, Object, Object>> getStringGroup() {
		return stringRedisTemplate.opsForStream().read(
			Consumer.from(RedisKey.STREAM_GROUP.getValue(), RedisKey.CONSUMER_NAME.getValue()),
			StreamReadOptions.empty().count(5).block(Duration.ofMillis(2000)),
			StreamOffset.create(RedisKey.STREAM_KEY.getValue(), ReadOffset.lastConsumed()));
	}

	public Set<String> getRetryCandidates() {
		long now = System.currentTimeMillis();
		return redisTemplate.opsForZSet().rangeByScore(RedisKey.RETRY_ZSET_KEY.getValue(), 0, now);
	}

	public Set<String> getAllCandidates() {
		return redisTemplate.opsForZSet().range(RedisKey.RETRY_ZSET_KEY.getValue(), 0, -1);
	}

	public void removeRetryCandidate(String json) {
		redisTemplate.opsForZSet().remove(RedisKey.RETRY_ZSET_KEY.getValue(), json);
	}

	// 사용자 id를 활용하여 Redis Stream과 ZSet에서 제거합니다.
	public void removeMemberFromAllRedisKeys(Long memberId) {
		acknowledgeMatch(memberId); // Stream 삭제 및 ack 처리
		removeRetryCandidateByMemberId(memberId); // ZSet 삭제
	}

	// 리트라이 큐에서 사용자id를 활용하여 제거합니다.
	public void removeRetryCandidateByMemberId(Long memberId) {
		// 모든 시점의 리트라이 큐 데이터 가져오기(매칭 시도중 새로고침하고 매칭을 시도했을때 업데이트 되지 않는 문제 해결을 위해)
		Set<String> retryCandidates = getAllCandidates();

		if (retryCandidates != null) {
			for (String json : retryCandidates) {
				try {
					MatchingFilterParsingDto existingDto = objectMapper.readValue(json, MatchingFilterParsingDto.class);
					if (existingDto.getMemberId().equals(memberId)) {
						removeRetryCandidate(json); // 해당 JSON 삭제
						log.info("[INFO] 기존 retry 데이터 삭제 완료 : {}", memberId);
					}
				} catch (JsonProcessingException e) {
					log.error("[ERROR] Retry 큐 데이터 파싱 실패 : {}", e.getMessage());
				}
			}
		}
	}

	//딜리트 큐에 사용자를 추가합니다.
	public void addToDeleteQueue(Long memberId) {
		try {
			redisTemplate.opsForSet().add(RedisKey.DELETE_QUEUE_KEY.getValue(), memberId.toString());
			// 딜리트 큐에서 ttl 설정
			redisTemplate.expire(RedisKey.DELETE_QUEUE_KEY.getValue(), Duration.ofSeconds(15));
			log.info("[INFO] 사용자 ID {}가 딜리트 큐에 추가되었습니다.", memberId);
		} catch (Exception e) {
			log.error("[ERROR] Redis에서 딜리트 큐에 오류 발생: {}", e.getMessage());
			throw new RuntimeException("매칭 취소 처리 중 오류가 발생했습니다.", e);
		}
	}

	// 딜리트 큐에 사용자가 있는지 확인합니다.
	public boolean isInDeleteQueue(Long memberId) {
		Boolean isMember = redisTemplate.opsForSet()
			.isMember(RedisKey.DELETE_QUEUE_KEY.getValue(), memberId.toString());
		return Boolean.TRUE.equals(isMember);
	}

	// 딜리트 큐에서 사용자를 제거합니다.
	public void deleteMemberToDeleteQueue(Long memberId) {
		redisTemplate.opsForSet().remove(RedisKey.DELETE_QUEUE_KEY.getValue(), memberId.toString());
	}

	public void addToMatchedUsers(Long memberId1, Long memberId2) {
		try {
			String timestamp = String.valueOf(System.currentTimeMillis());
			Map<String, String> matchedData = new HashMap<>();
			matchedData.put("memberId1", String.valueOf(memberId1));
			matchedData.put("memberId2", String.valueOf(memberId2));
			matchedData.put("timestamp", timestamp);

			String json = objectMapper.writeValueAsString(matchedData);

			redisTemplate.opsForZSet().add(RedisKey.MATCHED_ZSET_KEY.getValue(), json, Long.parseLong(timestamp));
		} catch (Exception e) {
			log.error("[ERROR] Redis에 매칭된 유저 추가 실패: {}", e.getMessage());
		}
	}

	public List<Map<String, String>> getTwentyMatchedUsers() {
		long now = System.currentTimeMillis();
		long threshold = now - 1000 * 60 * 10; // 10분 전 시간

		Set<String> matchedUsers = redisTemplate.opsForZSet()
			.rangeByScore(RedisKey.MATCHED_ZSET_KEY.getValue(), 0, threshold);

		if (matchedUsers == null || matchedUsers.isEmpty()) {
			return Collections.emptyList();
		}

		List<Map<String, String>> result = new ArrayList<>();

		for (String json : matchedUsers) {
			try {
				Map<String, String> matchedData = objectMapper.readValue(json, Map.class);
				result.add(matchedData);
			} catch (JsonProcessingException e) {
				log.error("[ERROR] 매칭된 유저 데이터 파싱 실패: {}", e.getMessage());
			}
		}
		return result;
	}

	public void updateMatchedUser(Map<String, String> json, Map<String, String> updateJson) {
		try {
			redisTemplate.opsForZSet()
				.remove(RedisKey.MATCHED_ZSET_KEY.getValue(), objectMapper.writeValueAsString(json));
			redisTemplate.opsForZSet()
				.add(RedisKey.MATCHED_ZSET_KEY.getValue(), objectMapper.writeValueAsString(updateJson),
					System.currentTimeMillis());
		} catch (Exception e) {
			log.error("[ERROR] Redis에 매칭된 유저 업데이트 실패: {}", e.getMessage());
		}
	}

	public void deleteMatchedUser(Map<String, String> deleteJson) {
		try {
			redisTemplate.opsForZSet()
				.remove(RedisKey.MATCHED_ZSET_KEY.getValue(), objectMapper.writeValueAsString(deleteJson));
		} catch (Exception e) {
			log.error("[ERROR] Redis에 매칭된 유저 삭제 실패: {}", e.getMessage());
		}
	}

	public boolean canExecuteReview(Long memberId1, Long memberId2) {
		log.info("[INFO] canExecuteReview 호출: memberId1={}, memberId2={}", memberId1, memberId2);
		String key = "canExecuteReview:" + memberId1 + ":" + memberId2;
		Long count = redisTemplate.opsForValue().increment(key);
		if (count == 1) {
			// 최초 실행일경우
			redisTemplate.expire(key, Duration.ofHours(2)); // 2시간 후 만료
		}
		return count <= 5; // 5회까지 허용
	}
}
