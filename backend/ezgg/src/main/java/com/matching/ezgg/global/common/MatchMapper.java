package com.matching.ezgg.global.common;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.data.riotApi.dto.MatchDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchMapper {
	private final ObjectMapper objectMapper;

	public MatchDto toMatchDto(String rawJson, Long memberId, String puuid) {
		try {
			JsonNode root = objectMapper.readTree(rawJson);

			// matchId 지정
			String riotMatchId = root.path("metadata").path("matchId").asText();

			// 타겟 유저 정보 노드 검색
			JsonNode targetMemberNode = findTargetMember(root.path("info").path("participants"), puuid);

			int kills = targetMemberNode.path("kills").asInt();
			int deaths = targetMemberNode.path("deaths").asInt();
			int assists = targetMemberNode.path("assists").asInt();
			String teamPosition = targetMemberNode.path("teamPosition").asText();
			String championName = targetMemberNode.path("championName").asText();
			boolean win = targetMemberNode.path("win").asBoolean();

			// Dto 생성, memberId는 따로 추가해야된다!
			return new MatchDto(
				memberId,
				riotMatchId,
				kills,
				deaths,
				assists,
				teamPosition,
				championName,
				win
			);

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("JSON → MatchDto 변환 실패", e);
		}
	}

	// participant 배열에서 puuid가 일치하는 노드 리턴
	private JsonNode findTargetMember(JsonNode participants, String puuid) {
		// map 혹은 stream으로 metadata node에 있는 puuid로 검색할 수도 있지만, 현재 상황(1명의 info만 검색)에서는 for 루프의 성능이 가장 좋다!
		for (JsonNode participant : participants) {
			if (puuid.equals(participant.path("puuid").asText())) {
				return participant;
			}
		}
		throw new IllegalArgumentException("해당 Match에서 멤버를 찾을 수 없습니다.");
	}
}
