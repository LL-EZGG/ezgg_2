package com.matching.ezgg.matching.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.api.domain.match.service.MatchService;
import com.matching.ezgg.api.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.api.domain.recentTwentyMatch.service.RecentTwentyMatchBuilderService;
import com.matching.ezgg.api.domain.recentTwentyMatch.service.RecentTwentyMatchService;
import com.matching.ezgg.api.dto.RecentTwentyMatchDto;
import com.matching.ezgg.api.service.ApiService;
import com.matching.ezgg.es.service.EsService;
import com.matching.ezgg.matching.dto.MatchingFilterDto;
import com.matching.ezgg.matching.dto.MemberInfoDto;
import com.matching.ezgg.matching.dto.PreferredPartnerDto;
import com.matching.ezgg.matching.dto.RecentTwentyMath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {
	private final MemberInfoService memberInfoService;
	private final MatchService matchService;
	private final ApiService apiService;
	private final RecentTwentyMatchService recentTwentyMatchService;
	private final RecentTwentyMatchBuilderService recentTwentyMatchBuilderService;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
	private final EsService esService;

	// 매칭 시작 시 호출
	public void startMatching(Long memberId, PreferredPartnerDto preferredPartnerDto) {
		log.info("매칭 시작! memberId = {}", memberId);
		// String puuid = memberInfoService.getMemberPuuidByMemberId(memberId);
		// updateAllAttributesOfMember(puuid); // 임시 주석

		// ------------- 임시 데이터 ---------------

		for (int i = 0; i < 5; i++) {
			MatchingFilterDto matchingFilterDtoDummy = createRandomMatchingData(i);
			try {
				String json = objectMapper.writeValueAsString(matchingFilterDtoDummy);
				long baseScore = System.currentTimeMillis();
				String zsetKey = "matching:queue";

				log.info("JSON 직렬화 성공: {}", json);
				esService.esPost(matchingFilterDtoDummy);
				// TODO Redis에 저장하는 로직 추가 (임시)
				// redisTemplate.opsForZSet().add(zsetKey, json, baseScore);
			} catch (Exception e) {
				log.error("JSON 직렬화 실패", e);
			}
		}

		// ---------------------------------------
	}

	// 매칭 시작 전 모든 데이터 업데이트
	public void updateAllAttributesOfMember(String puuid) {
		log.info("Riot Api로 모든 데이터 저장 시작: {}", puuid);
		// 티어, 승률 업데이트
		memberInfoService.updateWinRateNTier(apiService.getMemberWinRateNTier(puuid));

		// matchIds 업데이트 후 새롭게 추가된 matchId 리스트 리턴
		List<String> newlyAddedMatchIds = updateAndGetNewMatchIds(puuid, apiService.getMemberMatchIds(puuid));

		// 새로운 match들 저장
		for (String matchId : newlyAddedMatchIds) {
			matchService.save(apiService.getMemberMatch(puuid, matchId));
		}

		if (!newlyAddedMatchIds.isEmpty()) {
			saveRecentTwentyMatch(recentTwentyMatchBuilderService.buildDto(puuid));
		}
		log.info("Riot Api로 모든 데이터 저장 종료: {}", puuid);
	}

	private void createEsMatchingDocument() {
		//TODO
	}

	// 새로운 matchId가 없으면 null 리스트 리턴. 있으면 matchIds 업데이트 후 새로운 matchId 리스트 리턴
	public List<String> updateAndGetNewMatchIds(String puuid, List<String> fetchedMatchIds) {
		List<String> newlyAddedMatchIds = memberInfoService.extractNewMatchIds(puuid, fetchedMatchIds);

		if (newlyAddedMatchIds != null && !newlyAddedMatchIds.isEmpty()) {
			memberInfoService.updateMatchIds(puuid, fetchedMatchIds);
		}

		return newlyAddedMatchIds;
	}

	// recent_twenty_match 엔티티 업데이트 & 저장
	public void saveRecentTwentyMatch(RecentTwentyMatchDto recentTwentyMatchDto) {

		// 이미 존재하면 업데이트, 없으면 새롭게 저장 TODO Upsert 방식으로 수정
		if (recentTwentyMatchService.existsByMemberId(recentTwentyMatchDto.getMemberId())) {
			recentTwentyMatchService.updateRecentTwentyMatch(recentTwentyMatchDto);
		} else {
			recentTwentyMatchService.createNewRecentTwentyMatch(recentTwentyMatchDto);
		}
	}

	// ---------------------------------------- 더미데이터 생성 로직 --------------------------------
	// 랜덤 매칭 데이터 생성
	private MatchingFilterDto createRandomMatchingData(int index) {
		MatchingFilterDto dto = new MatchingFilterDto();
		dto.setMemberId((long)index);

		// MemberInfo 생성
		MemberInfoDto memberInfo = new MemberInfoDto();
		memberInfo.setRiotUsername("Player" + index);
		memberInfo.setRiotTag(getRandom(riotTags));
		memberInfo.setTier(getRandom(tiers));
		memberInfo.setTierNum(getRandom(tierNums));
		memberInfo.setWins(randomInt(10, 100));
		memberInfo.setLosses(randomInt(10, 100));
		dto.setMemberInfo(memberInfo);

		// PreferredPartner 생성
		PreferredPartnerDto preferredPartner = new PreferredPartnerDto();
		PreferredPartnerDto.WantLine wantLine = new PreferredPartnerDto.WantLine();
		wantLine.setMyLine(getRandom(lines));
		wantLine.setPartnerLine(getRandom(lines));
		preferredPartner.setWantLine(wantLine);
		PreferredPartnerDto.ChampionInfo championInfo = new PreferredPartnerDto.ChampionInfo();
		championInfo.setPreferredChampion(getRandom(champions));
		championInfo.setUnpreferredChampion(getRandom(champions));
		preferredPartner.setChampionInfo(championInfo);
		dto.setPreferredPartner(preferredPartner);

		// RecentTwentyMath 생성 (실제 애플리케이션에서는 null로 해도 될 것 같습니다)
		RecentTwentyMath recentTwentyMath = new RecentTwentyMath();
		recentTwentyMath.setKills(randomInt(30, 150));
		recentTwentyMath.setDeaths(randomInt(20, 100));
		recentTwentyMath.setAssists(randomInt(30, 200));

		// mostChampions 리스트 초기화
		recentTwentyMath.setMostChampions(new ArrayList<>());

		// MostChampion 생성
		for (int i = 0; i < 3; i++) {
			RecentTwentyMath.MostChampion mostChampion = new RecentTwentyMath.MostChampion();
			mostChampion.setChampionName(getRandom(champions));
			mostChampion.setKills(randomInt(5, 20));
			mostChampion.setDeaths(randomInt(1, 10));
			mostChampion.setAssists(randomInt(5, 20));
			mostChampion.setWins(randomInt(0, 20));
			mostChampion.setLosses(randomInt(0, 20));
			mostChampion.setTotalMatches(mostChampion.getWins() + mostChampion.getLosses());
			mostChampion.setWinRateOfChampion(
				(int)((double)mostChampion.getWins() / mostChampion.getTotalMatches() * 100));

			recentTwentyMath.getMostChampions().add(mostChampion);
		}

		dto.setRecentTwentyMatch(recentTwentyMath);

		return dto;
	}

	private <T> T getRandom(List<T> list) {
		return list.get(ThreadLocalRandom.current().nextInt(list.size()));
	}

	private int randomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	// 사전 정의된 값들
	private final List<String> lines = List.of("TOP", "MIDDLE", "JUNGLE", "BOTTOM", "UTILITY");
	private final List<String> champions = List.of("Aatrox", "Zed", "Lee Sin", "Ahri", "Jhin", "Lux", "Yasuo", "Thresh",
		"Ezreal", "Vayne", "Akali", "Darius", "Garen", "Katarina", "Riven", "Kai'Sa", "Jinx", "Rengar", "Kha'Zix",
		"Zyra");
	private final List<String> riotTags = List.of("KR1", "EUW", "NA1", "JP1");
	private final List<String> tiers = List.of("Iron", "Bronze", "Silver", "Gold", "Platinum", "EMERALD", "Diamond",
		"Master",
		"Grandmaster", "Challenger");
	private final List<String> tierNums = List.of("I", "II", "III", "IV", "V");

}
