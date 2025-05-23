package com.matching.ezgg.domain.matching.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.domain.matching.infra.es.service.EsMatchingFilter;
import com.matching.ezgg.domain.matching.infra.es.service.EsService;
import com.matching.ezgg.domain.matching.infra.redis.service.RedisService;
import com.matching.ezgg.domain.matching.infra.redis.stream.RedisStreamProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingProcessor {

	private final EsMatchingFilter esMatchingFilter;
	private final RedisStreamProducer redisStreamProducer;
	private final EsService esService;
	private final RedisService redisService;

	public void tryMatch(MatchingFilterParsingDto matchingFilterParsingDto) {
		try {
			if (matchingFilterParsingDto.getPreferredPartnerParsing() == null ||
				matchingFilterParsingDto.getPreferredPartnerParsing().getChampionInfo() == null) {
				log.warn("매칭 정보 누락: memberId={}, championInfo is null", matchingFilterParsingDto.getMemberId());
				// 필요한 경우 기본값 설정 또는 다른 처리
				redisStreamProducer.acknowledgeUser(matchingFilterParsingDto.getMemberId());
				return;
			}

			List<String> preferredPartnerChampions = matchingFilterParsingDto.getPreferredPartnerParsing()
				.getChampionInfo().getPreferredChampions();
			List<String> unpreferredPartnerChampions = matchingFilterParsingDto.getPreferredPartnerParsing()
				.getChampionInfo().getUnpreferredChampions();

			// es에서 매칭 상대 조회
			List<MatchingFilterParsingDto> matchingUsers = esMatchingFilter.findMatchingUsers(
				matchingFilterParsingDto.getPreferredPartnerParsing().getWantLine().getMyLine(),
				matchingFilterParsingDto.getPreferredPartnerParsing().getWantLine().getPartnerLine(),
				matchingFilterParsingDto.getMemberInfoParsing().getTier(),
				matchingFilterParsingDto.getMemberId(),
				preferredPartnerChampions,
				unpreferredPartnerChampions
			);

			if (matchingUsers.isEmpty()) {
				log.info("매칭 상대 없음 : {}", matchingFilterParsingDto.getMemberId());
				redisStreamProducer.acknowledgeUser(matchingFilterParsingDto.getMemberId());
				redisStreamProducer.retryLater(matchingFilterParsingDto);
				return;
			}

			MatchingFilterParsingDto bestMatchingUser = matchingUsers.getFirst(); // 매칭 점수가 가장 높은 유저
			log.info("매칭 성공! >>>>> {} : {}", matchingFilterParsingDto.getMemberId(), bestMatchingUser.getMemberId());

			redisService.addToMatchedUsers(matchingFilterParsingDto.getMemberId(), bestMatchingUser.getMemberId());

			// ES에서 매칭된 유저들의 데이터 삭제
			esService.deleteDocByMemberId(matchingFilterParsingDto.getMemberId());
			esService.deleteDocByMemberId(bestMatchingUser.getMemberId());

			redisStreamProducer.acknowledgeBothUser(matchingFilterParsingDto, bestMatchingUser);
			redisStreamProducer.removeRetryCandidate(matchingFilterParsingDto);
			redisStreamProducer.removeRetryCandidate(bestMatchingUser);
		} catch (Exception e) {
			log.error("매칭 처리 중 에러 발생 : {}", e.getMessage());
		}
	}
}
