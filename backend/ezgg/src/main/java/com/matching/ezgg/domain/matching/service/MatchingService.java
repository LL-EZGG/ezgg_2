package com.matching.ezgg.domain.matching.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.matching.ezgg.api.dto.MatchDto;
import com.matching.ezgg.api.dto.WinRateNTierDto;
import com.matching.ezgg.api.service.ApiService;
import com.matching.ezgg.domain.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.domain.matching.dto.MemberDataBundle;
import com.matching.ezgg.domain.matching.dto.MemberInfoParsingDto;
import com.matching.ezgg.domain.matching.dto.PreferredPartnerParsingDto;
import com.matching.ezgg.domain.matching.dto.RecentTwentyMatchParsingDto;
import com.matching.ezgg.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.domain.recentTwentyMatch.ChampionStat;
import com.matching.ezgg.es.service.EsService;
import com.matching.ezgg.redis.match.RedisStreamProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

	private final EsService esService;
	private final MemberInfoService memberInfoService;
	private final ApiService apiService;
	private final MatchingDataBulkSaveService matchingDataBulkSaveService;
	private final RedisStreamProducer redisStreamProducer;

	// 매칭 시작 시 호출
	public void startMatching(Long memberId, PreferredPartnerParsingDto preferredPartnerParsingDto) {
		log.info("매칭 시작! memberId = {}", memberId);

		// 모든 데이터 riot api로 저장 후 리턴
		MemberDataBundle memberDataBundle = updateAllAttributesOfMember(memberId);

		// memberDataBundle -> MatchingFilterDto 변환
		MatchingFilterParsingDto matchingFilterParsingDto = convertToMatchingFilterDto(memberDataBundle, memberId,
			preferredPartnerParsingDto);

		log.info("Json: {}", matchingFilterParsingDto.toString());

		esService.esPost(matchingFilterParsingDto);
		redisStreamProducer.sendMatchRequest(matchingFilterParsingDto);
	}

	// 매칭 시작 전 모든 데이터 업데이트
	public MemberDataBundle updateAllAttributesOfMember(Long memberId) {

		String puuid = memberInfoService.getMemberPuuidByMemberId(memberId);
		log.info("Riot Api로 모든 데이터 저장 시작: {}", puuid);

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

		// returnDto
		MemberDataBundle memberDataBundle = new MemberDataBundle();

		// api로 받아온 데이터 한 트랜잭션으로 저장하고 memberInfo 리턴
		memberDataBundle.setMemberInfo(matchingDataBulkSaveService.saveAllAggregatedData(
			memberId, memberWinRateNTier, fetchedMatchIds, matchDtoList, existsNewMatchIds
		));

		// recentTwentyMatch 저장
		memberDataBundle.setRecentTwentyMatch(matchingDataBulkSaveService.calculateAndSaveRecentTwentyMatch(
			existsNewMatchIds, puuid, memberId
		));

		log.info("Riot Api로 모든 데이터 저장 종료: {}", puuid);
		return memberDataBundle;
	}

	// 새로운 matchId가 없으면 null 리스트 리턴. 있으면 matchIds 업데이트 후 새로운 matchId 리스트 리턴
	public List<String> getNewMatchIds(String puuid, List<String> fetchedMatchIds) {
		return memberInfoService.extractNewMatchIds(puuid, fetchedMatchIds);
	}

	// memberDataBundle -> MatchingFilterDto 변환 메소드
	public MatchingFilterParsingDto convertToMatchingFilterDto(MemberDataBundle memberDataBundle, Long memberId,
		PreferredPartnerParsingDto preferredPartnerParsingDto) {
		MemberInfoParsingDto memberInfoParsingDto = MemberInfoParsingDto.builder()
			.riotUsername(memberDataBundle.getMemberInfo().getRiotUsername())
			.riotTag(memberDataBundle.getMemberInfo().getRiotTag())
			.tier(memberDataBundle.getMemberInfo().getTier())
			.tierNum(memberDataBundle.getMemberInfo().getTierNum())
			.wins(memberDataBundle.getMemberInfo().getWins())
			.losses(memberDataBundle.getMemberInfo().getLosses())
			.build();

		RecentTwentyMatchParsingDto recentTwentyMatchparsingDto = new RecentTwentyMatchParsingDto();

		if (memberDataBundle.getRecentTwentyMatch().getChampionStats() == null || memberDataBundle.getRecentTwentyMatch().getChampionStats().isEmpty()) {
			recentTwentyMatchparsingDto.setKills(0);
			recentTwentyMatchparsingDto.setDeaths(0);
			recentTwentyMatchparsingDto.setAssists(0);
			recentTwentyMatchparsingDto.setMostChampions(null);
		} else {
			Map<String, ChampionStat> championStats = memberDataBundle.getRecentTwentyMatch().getChampionStats();
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

			recentTwentyMatchparsingDto.setKills(memberDataBundle.getRecentTwentyMatch().getSumKills());
			recentTwentyMatchparsingDto.setDeaths(memberDataBundle.getRecentTwentyMatch().getSumDeaths());
			recentTwentyMatchparsingDto.setAssists(memberDataBundle.getRecentTwentyMatch().getSumAssists());
			recentTwentyMatchparsingDto.setMostChampions(mostChampions);
		}

		return MatchingFilterParsingDto.builder()
			.memberId(memberId)
			.preferredPartnerParsing(preferredPartnerParsingDto)
			.memberInfoParsing(memberInfoParsingDto)
			.recentTwentyMatchParsing(recentTwentyMatchparsingDto)
			.build();
	}
}
