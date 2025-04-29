package com.matching.ezgg.domain.matching.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.matching.ezgg.es.service.EsMatchingFilter;
import com.matching.ezgg.es.service.EsService;
import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.redis.match.RedisService;
import com.matching.ezgg.redis.match.RedisStreamProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingProcessor {

	private final EsMatchingFilter esMatchingFilter;
	private final RedisStreamProducer redisStreamProducer;
	private final RedisService redisService;
	private final EsService esService;

	public boolean tryMatch(MatchingFilterParsingDto matchingFilterParsingDto) {
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
				redisStreamProducer.retryLater(matchingFilterParsingDto);
				return false;
			}

			MatchingFilterParsingDto bestMatchingUser = matchingUsers.getFirst(); // 매칭 점수가 가장 높은 유저
			log.info("매칭 성공! >>>>> {} : {}", matchingFilterParsingDto.getMemberId(), bestMatchingUser.getMemberId());

			// ES에서 매칭된 유저들의 데이터 삭제
			esService.deleteDocByMemberId(matchingFilterParsingDto.getMemberId());
			esService.deleteDocByMemberId(bestMatchingUser.getMemberId());

			return true;
		} catch (Exception e) {
			log.error("매칭 처리 중 에러 발생 : {}", e.getMessage());
			return false;
		}
	}
}
