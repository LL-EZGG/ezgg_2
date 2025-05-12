package com.matching.ezgg.matching.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.matching.ezgg.data.riotApi.dto.MatchDto;
import com.matching.ezgg.data.riotApi.dto.WinRateNTierDto;
import com.matching.ezgg.data.riotApi.service.ApiService;
import com.matching.ezgg.matching.dto.MatchingFilterParsingDto;
import com.matching.ezgg.matching.dto.MemberDataBundleDto;
import com.matching.ezgg.matching.dto.MemberInfoParsingDto;
import com.matching.ezgg.matching.dto.PreferredPartnerParsingDto;
import com.matching.ezgg.matching.dto.RecentTwentyMatchParsingDto;
import com.matching.ezgg.data.memberInfo.service.MemberInfoService;
import com.matching.ezgg.data.recentTwentyMatch.entity.model.ChampionStat;
import com.matching.ezgg.matching.infra.es.service.EsService;
import com.matching.ezgg.matching.infra.redis.RedisStreamProducer;

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
		MemberDataBundleDto memberDataBundleDto = updateAllAttributesOfMember(memberId);

		// memberDataBundle -> MatchingFilterDto 변환
		MatchingFilterParsingDto matchingFilterParsingDto = convertToMatchingFilterDto(memberDataBundleDto, memberId,
			preferredPartnerParsingDto);

		log.info("Json: {}", matchingFilterParsingDto.toString());

		esService.esPost(matchingFilterParsingDto);
		redisStreamProducer.sendMatchRequest(matchingFilterParsingDto);
	}

	// 매칭 시작 전 모든 데이터 업데이트
	public MemberDataBundleDto updateAllAttributesOfMember(Long memberId) {

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
		MemberDataBundleDto memberDataBundleDto = new MemberDataBundleDto();

		// api로 받아온 데이터 한 트랜잭션으로 저장하고 memberInfo 리턴
		memberDataBundleDto.setMemberInfoDto(matchingDataBulkSaveService.saveAllAggregatedData(
			memberId, memberWinRateNTier, fetchedMatchIds, matchDtoList, existsNewMatchIds
		));

		// recentTwentyMatchDto 저장
		memberDataBundle.setRecentTwentyMatchDto(
			RecentTwentyMatchDto.toDto(matchingDataBulkSaveService.calculateAndSaveRecentTwentyMatch(
				existsNewMatchIds, puuid, memberId
			)));

		log.info("Riot Api로 모든 데이터 저장 종료: {}", puuid);
		return memberDataBundleDto;
	}

	// 새로운 matchId가 없으면 null 리스트 리턴. 있으면 matchIds 업데이트 후 새로운 matchId 리스트 리턴
	public List<String> getNewMatchIds(String puuid, List<String> fetchedMatchIds) {
		return memberInfoService.extractNewMatchIds(puuid, fetchedMatchIds);
	}

	// memberDataBundle -> MatchingFilterDto 변환 메소드
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

		RecentTwentyMatchParsingDto recentTwentyMatchparsingDto = new RecentTwentyMatchParsingDto();

		if (memberDataBundleDto.getRecentTwentyMatchDto().getChampionStats() == null || memberDataBundleDto.getRecentTwentyMatchDto().getChampionStats().isEmpty()) {
			recentTwentyMatchparsingDto.setKills(0);
			recentTwentyMatchparsingDto.setDeaths(0);
			recentTwentyMatchparsingDto.setAssists(0);
			recentTwentyMatchparsingDto.setMostChampions(null);
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

			recentTwentyMatchparsingDto.setKills(memberDataBundleDto.getRecentTwentyMatchDto().getSumKills());
			recentTwentyMatchparsingDto.setDeaths(memberDataBundleDto.getRecentTwentyMatchDto().getSumDeaths());
			recentTwentyMatchparsingDto.setAssists(memberDataBundleDto.getRecentTwentyMatchDto().getSumAssists());
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
