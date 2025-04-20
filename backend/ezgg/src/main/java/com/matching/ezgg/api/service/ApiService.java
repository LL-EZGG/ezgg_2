package com.matching.ezgg.api.service;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.matching.ezgg.api.dto.PuuidDto;
import com.matching.ezgg.api.dto.WinRateNTierDto;
import com.matching.ezgg.global.exception.RiotAccountNotFoundException;
import com.matching.ezgg.global.exception.RiotApiException;
import com.matching.ezgg.global.exception.RiotTierNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiService {

	@Qualifier("asia")
	private final RestTemplate asiaRestTemplate;
	@Qualifier("kr")
	private final RestTemplate krRestTemplate;
	private final String apiKey;

	public ApiService(@Qualifier("asia") RestTemplate asiaRestTemplate, @Qualifier("kr") RestTemplate krRestTemplate,
		@Value("${api.key}") String apiKey) {
		this.asiaRestTemplate = asiaRestTemplate;
		this.krRestTemplate = krRestTemplate;
		this.apiKey = apiKey;
	}

	//riot/account/v1/accounts/by-riot-id/{riot-id}/{tag}?api_key=
	// 유저 id, tag -> Riot api -> puuid를 수령
	public String getMemberPuuid(String riotId, String tag) {
		log.info("puuid 조회 시작: {}, {}", riotId, tag);

		try {
			String url = String.format(
				"/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s",
				riotId, tag, apiKey
			);

			PuuidDto dto = asiaRestTemplate.getForObject(url, PuuidDto.class);
			if (dto == null || dto.getPuuid() == null) {
				throw new RiotAccountNotFoundException(riotId, tag);
			}
			return dto.getPuuid();

		} catch (HttpClientErrorException.NotFound e) {
			throw new RiotAccountNotFoundException(riotId, tag);
		} catch (RestClientException e) {
			throw new RiotApiException("puuid 조회 Riot Api 실패");
		}
	}

	// /lol/league/v4/entries/by-puuid/{encryptedPUUID}
	// puuid -> Riot api -> tier, rank, wins, losses 수령
	public WinRateNTierDto getMemberWinRateNTier(String puuid) {
		log.info("승률/티어 조회 시작: {}", puuid);

		try {
			String url = String.format(
				"/lol/league/v4/entries/by-puuid/%s?api_key=%s",
				puuid, apiKey
			);

			WinRateNTierDto[] dtoArr = krRestTemplate.getForObject(url, WinRateNTierDto[].class);

			if (dtoArr == null || dtoArr.length == 0) {
				throw new RiotTierNotFoundException(puuid);
			}

			// Riot API에서 배열 구조로 게임 큐타입 단위로 티어/승률 객체 전송 -> 개인/2인 랭크 큐타입 데이터만 저장
			WinRateNTierDto winRateNTierDto = Arrays.stream(dtoArr)
				.filter(dto -> "RANKED_SOLO_5x5".equalsIgnoreCase(dto.getQueueType()))
				.findFirst()
				.orElseThrow(() -> new RiotTierNotFoundException(puuid));

			return winRateNTierDto;

		} catch (HttpClientErrorException.NotFound e) {
			throw new RiotTierNotFoundException(puuid);
		} catch (RestClientException e){
			throw new RiotApiException("승률/티어 조회 Riot Api 실패");
		}
	}

	//lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count=20&api_key=apiKey
	public ArrayList<String> getMemberMatchIds(String puuid) {
		log.info("matchIds 조회 시작");

		try {
			String url = String.format(
				"/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=20&api_key=%s",
				puuid, apiKey
			);

			return asiaRestTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<ArrayList<String>>() {
				}
			).getBody();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();//TODO
		}

	}

	//TODO DB에 저장되어 있던 매치 아이디들과 조회한 매치 아이디들 비교
	// public Mono<List<String>> compareMatchIds(MatchIdsDto matchIdsDto) {
	// 	log.info("matchIds 비교 시작");
	// 	Mono<List<String>> oldMatchIds = getMatchIds(
	//
	//
	// }

	//lol/match/v5/matches/{matchId}?api_key=
	public String getMatchInfo(String matchId) {
		log.info("matchInfo 조회 시작");

		String url = String.format(
			"/lol/match/v5/matches/%s?api_key=%s",
			matchId, apiKey
		);

		return asiaRestTemplate.getForObject(url, String.class);
	}

}
