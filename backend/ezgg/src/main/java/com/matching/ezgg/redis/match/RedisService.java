package com.matching.ezgg.redis.match;

import static com.matching.ezgg.redis.match.RedisKey.*;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public void saveMatchRequest(MatchingFilterParsingDto matchingFilterParsingDto) {
        try {
            String memberId = String.valueOf(matchingFilterParsingDto.getMemberId());
            
            // 기존 데이터 확인
            String existingStreamId = (String) redisTemplate.opsForHash().get(STREAM_ID_HASH_KEY.getValue(), memberId);
            if (existingStreamId == null) {
                String json = objectMapper.writeValueAsString(matchingFilterParsingDto);
                Map<String, String> message = new HashMap<>();
                message.put("data", json);

                RecordId recordId = redisTemplate.opsForStream().add(STREAM_KEY.getValue(), message);
                redisTemplate.opsForHash().put(STREAM_ID_HASH_KEY.getValue(), memberId, recordId.getValue());

                log.info("Redis Stream 매칭 요청 저장 완료 : {}", matchingFilterParsingDto.getMemberId());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Stream 등록 실패", e);
        }
    }

    private boolean isDataEqual(MatchingFilterParsingDto dto1, MatchingFilterParsingDto dto2) {
        // 필수 필드만 비교하여 데이터 변경 여부 확인
        return dto1.getMemberId().equals(dto2.getMemberId()) &&
               dto1.getPreferredPartnerParsing().getWantLine().getMyLine().equals(dto2.getPreferredPartnerParsing().getWantLine().getMyLine()) &&
               dto1.getPreferredPartnerParsing().getWantLine().getPartnerLine().equals(dto2.getPreferredPartnerParsing().getWantLine().getPartnerLine()) &&
               dto1.getMemberInfoParsing().getTier().equals(dto2.getMemberInfoParsing().getTier());
    }

    public void acknowledgeMatch(Long memberId, Long matchedMemberId) {
        try {
            String memberIdStr1 = String.valueOf(memberId);
            String memberIdStr2 = String.valueOf(matchedMemberId);
            String streamId1 = (String) redisTemplate.opsForHash().get(STREAM_ID_HASH_KEY.getValue(), memberIdStr1);
            String streamId2 = (String) redisTemplate.opsForHash().get(STREAM_ID_HASH_KEY.getValue(), memberIdStr2);

            if (streamId1 != null && streamId2 != null) {
                redisTemplate.opsForStream().acknowledge(STREAM_KEY.getValue(), STREAM_GROUP.getValue(), streamId1);
                redisTemplate.opsForStream().acknowledge(STREAM_KEY.getValue(), STREAM_GROUP.getValue(), streamId2);
                redisTemplate.opsForStream().delete(STREAM_KEY.getValue(), streamId1);
                redisTemplate.opsForStream().delete(STREAM_KEY.getValue(), streamId2);
                redisTemplate.opsForHash().delete(STREAM_ID_HASH_KEY.getValue(), memberIdStr1);
                redisTemplate.opsForHash().delete(STREAM_ID_HASH_KEY.getValue(), memberIdStr2);

                // 각 유저에게 맞는 결과 전송
                messagingTemplate.convertAndSendToUser(memberId.toString(), "/queue/matching", matchedMemberId);
                messagingTemplate.convertAndSendToUser(matchedMemberId.toString(), "/queue/matching", memberId);
            }
        } catch (Exception e) {
            log.error("Stream 처리 중 에러 발생 : {}", e.getMessage());
        }
    }

    public void retryMatchRequest(MatchingFilterParsingDto matchingFilterParsingDto) {
        try {
            String memberId = String.valueOf(matchingFilterParsingDto.getMemberId());
            
            // 기존 retry 데이터 확인
            Set<String> retryCandidates = getRetryCandidates();
            if (retryCandidates != null) {
                for (String json : retryCandidates) {
                    try {
                        MatchingFilterParsingDto existingDto = objectMapper.readValue(json, MatchingFilterParsingDto.class);
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

            redisTemplate.opsForZSet().add(RETRY_ZSET_KEY.getValue(), json, retryTime);
            log.info("딜레이 큐에 등록 완료 ({}초 후 재시도 예정) : {}", retryTime/1000, memberId);
        } catch (JsonProcessingException e) {
            log.error("딜레이 큐 등록 실패 : {}", e.getMessage());
        }
    }

    public void createStringGroup() {
        stringRedisTemplate.opsForStream().createGroup(STREAM_KEY.getValue(), ReadOffset.latest(), STREAM_GROUP.getValue());
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
