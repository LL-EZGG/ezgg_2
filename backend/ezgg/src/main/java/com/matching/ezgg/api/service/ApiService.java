package com.matching.ezgg.Api.service;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.matching.ezgg.Api.dto.MatchIdsDto;
import com.matching.ezgg.Api.dto.PuuidDto;
import com.matching.ezgg.Api.dto.WinRateNTierDto;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ApiService {

	private final WebClient asiaWebClient;
	private final WebClient krWebClient;
	private final String apiKey;
	// private final WinRateNTierAdapter tierAdapter;

	//Member에서 가져오기 전 임시 데이터
	MatchIdsDto matchIds = new MatchIdsDto(
		new ArrayList<>(Arrays.asList(
			"KR_7601768141", "KR_7601734513", "KR_7601705129", "KR_7601656394", "KR_7601613169",
			"KR_7600470456", "KR_7600434924", "KR_7600399950", "KR_7600362953", "KR_7600316442",
			"KR_7600285732", "KR_7600101729", "KR_7599633637", "KR_7599216701", "KR_7599167828",
			"KR_7599149069", "KR_7599089836", "KR_7599045765", "KR_7598920436", "KR_7598839696")));
	PuuidDto puuid = new PuuidDto("35XPfSvBPYGbS38jEmjVgCrLlZu-PO9yP5ajqMMH-Xec3o9nQ3PkqSBU7lAVQo1-Sa3e74aFxcpRPg");

	public ApiService(@Qualifier("asia") WebClient asiaWebClient, @Qualifier("kr") WebClient krWebClient,
		@Value("${api.key}") String apiKey) {
		this.asiaWebClient = asiaWebClient;
		this.krWebClient = krWebClient;
		this.apiKey = apiKey;

	}
	// public ApiService(WebClient webClient, @Value("${api.key}") String apiKey, WinRateNTierAdapter tierAdapter) {
	// 	this.webClient = webClient;
	// 	this.apiKey = apiKey;
	// 	this.tierAdapter = tierAdapter;
	// }

	//riot/account/v1/accounts/by-riot-id/{riot-id}/{tag}?api_key=
	//
	public Mono<String> getMemberPuuid(String riotId, String tag) {
		log.info("puuid 조회 시작: {}, {}", riotId, tag);
		return asiaWebClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/riot/account/v1/accounts/by-riot-id/{riotId}/{tag}")
				.queryParam("api_key", apiKey)
				.build(riotId, tag))
			.retrieve()
			.bodyToMono(PuuidDto.class)
			.map(PuuidDto::getPuuid);
		//저장 로직 추가 필요
	}

	//회원가입할 때 puuid는 받아옴
	//회원가입 이후엔 DB에서 puuid 가져올거임
	// public Mono<List<String>> getMemberStat(Mono<String> puuid) {
	// 	// return getMatchIds(getMemberPuuid(riotId, tag)); //테스트용 임시
	// 	/*
	//
	// 	 */
	// }

	// /lol/league/v4/entries/by-puuid/{encryptedPUUID}
	// puuid가 DB에 있는 경우
	public Mono<WinRateNTierDto> getMemberWinRateNTier(Mono<String> puuidMono) {
		log.info("승률과 티어 조회 시작");
		return puuidMono.flatMap(puuid -> {
			return krWebClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/lol/league/v4/entries/by-puuid/{puuid}")
					.queryParam("api_key", apiKey)
					.build(puuid))
				.retrieve()
				.bodyToFlux(WinRateNTierDto.class)
				.next();
			//저장 로직 추가 필요
		});

	}

	//lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count=20&api_key=apiKey
	public Mono<ArrayList<String>> getMatchIds(Mono<String> puuidMono) {
		log.info("matchIds 조회 시작");
		return puuidMono.flatMap(puuid -> {
			return asiaWebClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/lol/match/v5/matches/by-puuid/{puuid}/ids")
					.queryParam("start", 0)
					.queryParam("count", 20)
					.queryParam("api_key", apiKey)
					.build(puuid))
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<ArrayList<String>>() {
				}); //ParameterizedTypeReference는 타입 정보를 유지한 상태로 Webclient에 전달하기 위함
			//저장 로직 추가 필요
		});
	}

	//DB에 저장되어 있던 매치 아이디들과 조회한 매치 아이디들 비교

	// public Mono<List<String>> compareMatchIds(MatchIdsDto matchIdsDto) {
	// 	log.info("matchIds 비교 시작");
	// 	Mono<List<String>> oldMatchIds = getMatchIds(
	//
	//
	// }

	//lol/match/v5/matches/{matchId}?api_key=
	public Mono<String> getMatchInfo(String matchId) {
		log.info("matchInfo 조회 시작");
		return asiaWebClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/lol/match/v5/matches/{matchId}")
				.queryParam("api_key", apiKey)
				.build(matchId))
			.retrieve()
			.bodyToMono(String.class);
	}

}
