package com.matching.ezgg.matching.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.matching.ezgg.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.matching.infra.es.service.EsMatchingFilter;
import com.matching.ezgg.matching.infra.redis.stream.RedisStreamProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingProcessor {

	private final EsMatchingFilter esMatchingFilter;
	private final RedisStreamProducer redisStreamProducer;

	public void tryMatch(MatchingFilterParsingDto matchingFilterParsingDto) {
		try {
			// es에서 매칭 상대 조회
			List<MatchingFilterParsingDto> matchingUsers = esMatchingFilter.findMatchingUsers(
				matchingFilterParsingDto.getPreferredPartnerParsing().getWantLine().getMyLine(),
				matchingFilterParsingDto.getPreferredPartnerParsing().getWantLine().getPartnerLine(),
				matchingFilterParsingDto.getMemberInfoParsing().getTier(),
				matchingFilterParsingDto.getMemberId(),
				matchingFilterParsingDto.getPreferredPartnerParsing().getChampionInfo().getPreferredChampion(),
				matchingFilterParsingDto.getPreferredPartnerParsing().getChampionInfo().getUnpreferredChampion()
			);

			if (matchingUsers.isEmpty()) {
				log.info("매칭 상대 없음 : {}", matchingFilterParsingDto.getMemberId());
				redisStreamProducer.acknowledgeUser(matchingFilterParsingDto.getMemberId());
				redisStreamProducer.retryLater(matchingFilterParsingDto);
				return;
			}

			MatchingFilterParsingDto bestMatchingUser = matchingUsers.getFirst(); // 매칭 점수가 가장 높은 유저
			log.info("매칭 성공! >>>>> {} : {}", matchingFilterParsingDto.getMemberId(), bestMatchingUser.getMemberId());

			redisStreamProducer.acknowledgeBothUser(matchingFilterParsingDto, bestMatchingUser);
			redisStreamProducer.removeRetryCandidate(matchingFilterParsingDto);
			redisStreamProducer.removeRetryCandidate(bestMatchingUser);
		} catch (Exception e) {
			log.error("매칭 처리 중 에러 발생 : {}", e.getMessage());
		}
	}
}
