package com.matching.ezgg.matching.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.matching.ezgg.api.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.api.dto.MatchDto;
import com.matching.ezgg.api.dto.WinRateNTierDto;
import com.matching.ezgg.api.service.ApiService;
import com.matching.ezgg.matching.dto.MemberDataBundle;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private final ApiService apiService;
	private final MatchingDataBulkSaveService matchingDataBulkSaveService;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;


	// 매칭 시작 시 호출
	public void startMatching(Long memberId, PreferredPartnerDto preferredPartnerDto) {
		log.info("매칭 시작! memberId = {}", memberId);
  

		// MemberDataBundle memberDataBundle = updateAllAttributesOfMember(memberId);//TODO
		//TODO createEsMatchingDocument(), StartMatchingByDocuments(), ...


		//TODO memberDataBundle -> matchingFilterDto 생성 로직 추가

		// ------------- 임시 데이터 ---------------

		for (int i = 0; i < 5; i++) {
			MatchingFilterDto matchingFilterDtoDummy = createRandomMatchingData(i);
			try {
				String json = objectMapper.writeValueAsString(matchingFilterDtoDummy);
				long baseScore = System.currentTimeMillis();
				String zsetKey = "matching:queue";

				log.info("JSON 직렬화 성공: {}", json);
				// TODO es에 저장하는 로직 추가 (임시)

				// TODO Redis에 저장하는 로직 추가 (임시)
				// redisTemplate.opsForZSet().add(zsetKey, json, baseScore);
			} catch (Exception e) {
				log.error("JSON 직렬화 실패", e);
			}
		}

		// ---------------------------------------

	}

	// 매칭 시작 전 모든 데이터 업데이트
	public MemberDataBundle updateAllAttributesOfMember(Long memberId){

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

	private void createEsMatchingDocument(){
		//TODO
	}

	// 새로운 matchId가 없으면 null 리스트 리턴. 있으면 matchIds 업데이트 후 새로운 matchId 리스트 리턴
	public List<String> getNewMatchIds(String puuid, List<String> fetchedMatchIds) {
		return memberInfoService.extractNewMatchIds(puuid, fetchedMatchIds);
	}


	// TODO 이 아래로는 전부 더미데이터 생성 로직. 추후 삭제 필요
	// ---------------------------------------- 더미데이터 생성 로직 --------------------------------
	// 랜덤 매칭 데이터 생성
	private MatchingFilterDto createRandomMatchingData(int index) {
		MatchingFilterDto dto = new MatchingFilterDto();
		dto.setMemberId((long) index);
		
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
		for(int i = 0; i < 3; i++) {
			RecentTwentyMath.MostChampion mostChampion = new RecentTwentyMath.MostChampion();
			mostChampion.setChampionName(getRandom(champions));
			mostChampion.setKills(randomInt(5, 20));
			mostChampion.setDeaths(randomInt(1, 10));
			mostChampion.setAssists(randomInt(5, 20));
			mostChampion.setWins(randomInt(0, 20));
			mostChampion.setLosses(randomInt(0, 20));
			mostChampion.setTotalMatches(mostChampion.getWins() + mostChampion.getLosses());
			mostChampion.setWinRateOfChampion((int) ((double) mostChampion.getWins() / mostChampion.getTotalMatches() * 100));

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
	private final List<String> lines = List.of("top", "mid", "jungle", "adc", "support");
	private final List<String> champions = List.of("Aatrox", "Zed", "Lee Sin", "Ahri", "Jhin", "Lux", "Yasuo", "Thresh", "Ezreal", "Vayne", "Akali", "Darius", "Garen", "Katarina", "Riven", "Kai'Sa", "Jinx", "Rengar", "Kha'Zix", "Zyra");
	private final List<String> riotTags = List.of("KR1", "EUW", "NA1", "JP1");
	private final List<String> tiers = List.of("Iron", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "Challenger");
	private final List<String> tierNums = List.of("I", "II", "III", "IV", "V");

}
