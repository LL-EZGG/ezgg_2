package com.matching.ezgg.domain.matching.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.domain.matching.dto.MemberDataBundleDto;
import com.matching.ezgg.domain.matching.dto.MemberInfoParsingDto;
import com.matching.ezgg.domain.matching.dto.PreferredPartnerParsingDto;
import com.matching.ezgg.domain.matching.dto.RecentTwentyMatchParsingDto;
import com.matching.ezgg.domain.matching.infra.es.service.ElasticSearchService;
import com.matching.ezgg.domain.matching.infra.redis.service.RedisService;
import com.matching.ezgg.domain.matching.infra.redis.state.MatchingStateManager;
import com.matching.ezgg.domain.memberInfo.dto.MemberInfoDto;
import com.matching.ezgg.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.domain.recentTwentyMatch.dto.RecentTwentyMatchDto;
import com.matching.ezgg.domain.recentTwentyMatch.entity.model.ChampionStat;
import com.matching.ezgg.domain.riotApi.dto.MatchDto;
import com.matching.ezgg.domain.riotApi.dto.WinRateNTierDto;
import com.matching.ezgg.domain.riotApi.service.ApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

	private final ElasticSearchService elasticSearchService;
	private final MemberInfoService memberInfoService;
	private final ApiService apiService;
	private final MatchingDataBulkSaveService matchingDataBulkSaveService;
	private final MatchingStateManager matchingStateManager;
	private final RedisService redisService;

	/**
	 * 매칭을 시작할 때 호출되는 진입점 메서드
	 * <ol>
	 *   <li>Riot API로부터 최신 데이터를 수집·저장</li>
	 *   <li>ES/Redis에 존재하는 기존 매칭 정보를 초기화</li>
	 *   <li>새로운 매칭 조건을 ES에 저장하고 매칭 대기 상태로 등록</li>
	 * </ol>
	 *
	 * @param memberId                   매칭을 요청한 회원의 ID
	 * @param preferredPartnerParsingDto 사용자가 입력한 선호 파트너 조건 DTO
	 */
	public void startMatching(Long memberId, PreferredPartnerParsingDto preferredPartnerParsingDto) {
		log.info("[INFO] 매칭 시작! memberId = {}", memberId);
		log.info("[INFO] 선호 파트너 조건: {}", preferredPartnerParsingDto.getUserPreferenceText());
		// 모든 데이터 riot api로 저장 후 리턴
		MemberDataBundleDto memberDataBundleDto = updateAllAttributesOfMember(memberId);

		// memberDataBundle -> MatchingFilterDto 변환
		MatchingFilterParsingDto matchingFilterParsingDto = convertToMatchingFilterDto(memberDataBundleDto, memberId,
			preferredPartnerParsingDto);

		// 매칭 전 ES, Redis 사용자 정보 모두 제거
		elasticSearchService.deleteDocByMemberId(memberId);
		redisService.deleteMatchingState(memberId);
		redisService.removeUserFromRetrySet(memberId);

		// 매칭 시작 - ES에 document 저장, redis에 memberId 저장
		elasticSearchService.postDoc(matchingFilterParsingDto);
		matchingStateManager.addUserToMatchingState(memberId);
	}

	/**
	 * 매칭을 시작하기 전에 해당 회원의 모든 게임 관련 데이터를 최신 상태로 업데이트하는 메서드
	 * Riot API 호출부터 데이터 저장까지 한 번에 수행하며,
	 * 갱신된 정보들을 {@link MemberDataBundleDto} 형태로 반환한다.
	 *
	 * @param memberId 회원 ID
	 * @return 최신화된 회원 통합 데이터 번들 DTO
	 */
	public MemberDataBundleDto updateAllAttributesOfMember(Long memberId) {

		String puuid = memberInfoService.getMemberPuuidByMemberId(memberId);
		log.info("[INFO] Riot Api로 모든 데이터 저장 시작: {}", puuid);

		// 티어+승률/matchIds api 요청해서 메모리에 저장
		WinRateNTierDto memberWinRateNTier = apiService.getMemberWinRateNTier(puuid);
		List<String> fetchedMatchIds = apiService.getMemberMatchIds(puuid);
		List<String> newlyAddedMatchIds = getNewMatchIds(puuid, fetchedMatchIds);
		boolean existsNewMatchIds = !newlyAddedMatchIds.isEmpty();

		// matchInfo api 요청해서 메모리에 저장
		List<MatchDto> matchDtoList = new ArrayList<>();
		for (String matchId : newlyAddedMatchIds) {
			MatchDto matchInfo = apiService.getMemberMatch(memberId, puuid, matchId);
			matchDtoList.add(matchInfo);
		}

		MemberInfoDto memberInfoDto = matchingDataBulkSaveService.saveAllAggregatedData(
			memberId, memberWinRateNTier, fetchedMatchIds, matchDtoList, existsNewMatchIds
		);

		RecentTwentyMatchDto recentTwentyMatchDto = matchingDataBulkSaveService.calculateAndSaveRecentTwentyMatch(
			existsNewMatchIds, puuid, memberId
		);

		// returnDto
		MemberDataBundleDto memberDataBundleDto = MemberDataBundleDto.builder()
			.memberInfoDto(memberInfoDto)
			.recentTwentyMatchDto(recentTwentyMatchDto)
			.build();

		// api로 받아온 데이터 한 트랜잭션으로 저장하고 memberInfo 리턴

		log.info("[INFO] Riot Api로 모든 데이터 저장 종료: {}", puuid);
		return memberDataBundleDto;
	}

	/**
	 * Riot API에서 가져온 경기 ID 목록과 기존 저장된 경기 ID를 비교하여
	 * 새롭게 추가되어야 할 경기 ID만 추출하는 메서드
	 *
	 * @param puuid           Riot PUUID
	 * @param fetchedMatchIds Riot API로부터 조회한 최근 경기 ID 리스트
	 * @return 새롭게 추가된 경기 ID 리스트 (없으면 빈 리스트 반환)
	 */
	public List<String> getNewMatchIds(String puuid, List<String> fetchedMatchIds) {
		return memberInfoService.extractNewMatchIds(puuid, fetchedMatchIds);
	}

	/**
	 * 회원 통합 데이터 번들을 Elasticsearch에 저장할 수 있는
	 * {@link MatchingFilterParsingDto} 형식으로 변환하는 메서드
	 *
	 * @param memberDataBundleDto       통합 데이터 번들
	 * @param memberId                  회원 ID
	 * @param preferredPartnerParsingDto 선호 파트너 조건 DTO
	 * @return ES 저장용 매칭 필터 DTO
	 */
	public MatchingFilterParsingDto convertToMatchingFilterDto(MemberDataBundleDto memberDataBundleDto, Long memberId,
		PreferredPartnerParsingDto preferredPartnerParsingDto) {
		MemberInfoParsingDto memberInfoParsingDto = MemberInfoParsingDto.builder()
			.riotUsername(memberDataBundleDto.getMemberInfoDto().getRiotUsername())
			.riotTag(memberDataBundleDto.getMemberInfoDto().getRiotTag())
			.tier(memberDataBundleDto.getMemberInfoDto().getTier())
			.tierNum(memberDataBundleDto.getMemberInfoDto().getTierNum())
			.wins(memberDataBundleDto.getMemberInfoDto().getWins())
			.losses(memberDataBundleDto.getMemberInfoDto().getLosses())
			.build();

		RecentTwentyMatchParsingDto recentTwentyMatchparsingDto;

		if (memberDataBundleDto.getRecentTwentyMatchDto().getChampionStats() == null
			|| memberDataBundleDto.getRecentTwentyMatchDto().getChampionStats().isEmpty()) {
			recentTwentyMatchparsingDto = RecentTwentyMatchParsingDto.builder()
				.kills(0)
				.deaths(0)
				.assists(0)
				.mostChampions(null)
				.build();
		} else {
			Map<String, ChampionStat> championStats = memberDataBundleDto.getRecentTwentyMatchDto().getChampionStats();
			List<RecentTwentyMatchParsingDto.MostChampion> mostChampions = new ArrayList<>();

			for (ChampionStat value : championStats.values()) {

				mostChampions.add(RecentTwentyMatchParsingDto.MostChampion.builder()
					.championName(value.getChampionName())
					.kills(value.getKills())
					.deaths(value.getDeaths())
					.assists(value.getAssists())
					.wins(value.getWins())
					.losses(value.getLosses())
					.totalMatches(value.getTotal())
					.winRateOfChampion(value.getWinRateOfChampion())
					.build());
			}

			recentTwentyMatchparsingDto = RecentTwentyMatchParsingDto.builder()
				.kills(memberDataBundleDto.getRecentTwentyMatchDto().getSumKills())
				.deaths(memberDataBundleDto.getRecentTwentyMatchDto().getSumDeaths())
				.assists(memberDataBundleDto.getRecentTwentyMatchDto().getSumAssists())
				.mostChampions(mostChampions)
				.topAnalysis(memberDataBundleDto.getRecentTwentyMatchDto().getTopAnalysis())
				.jugAnalysis(memberDataBundleDto.getRecentTwentyMatchDto().getJugAnalysis())
				.midAnalysis(memberDataBundleDto.getRecentTwentyMatchDto().getMidAnalysis())
				.adAnalysis(memberDataBundleDto.getRecentTwentyMatchDto().getAdAnalysis())
				.supAnalysis(memberDataBundleDto.getRecentTwentyMatchDto().getSupAnalysis())
				.build();
		}

		return MatchingFilterParsingDto.builder()
			.memberId(memberId)
			.preferredPartnerParsing(preferredPartnerParsingDto)
			.memberInfoParsing(memberInfoParsingDto)
			.recentTwentyMatchParsing(recentTwentyMatchparsingDto)
			.build();
	}

	/**
	 * 사용자가 매칭을 취소할 때 호출하는 메서드
	 * <ul>
	 *   <li>Redis 스트림/큐 및 ES 문서에서 해당 회원의 모든 정보를 제거</li>
	 *   <li>오류 발생 시 런타임 예외로 래핑하여 상위 계층으로 전달.</li>
	 * </ul>
	 *
	 * @param memberId 매칭 취소를 요청한 회원 ID
	 * @throws RuntimeException 매칭 취소 도중 예기치 못한 문제가 발생한 경우
	 */
	public void stopMatching(Long memberId) {
		log.info("[INFO] 사용자 ID {}의 매칭 취소 요청 처리 중", memberId);

		try {
			redisService.addToDeleteQueue(memberId);
			matchingStateManager.removeAllRedisKeysByMemberId(memberId); // Redis Stream에서 사용자 제거
			elasticSearchService.deleteDocByMemberId(memberId);       // ES에서 사용자 문서 삭제
			log.info("[INFO] 사용자 ID {}의 매칭 취소 완료", memberId);
		} catch (Exception e) {
			log.error("[ERROR] 사용자 ID {}의 매칭 취소 중 오류 발생: {}", memberId, e.getMessage());
			throw new RuntimeException("매칭 취소 처리 중 오류가 발생했습니다.", e);
		}
	}
}
