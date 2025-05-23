package com.matching.ezgg.domain.riotApi.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.GlobalMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.JugMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.LanerMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.SupMatchParsingDto;
import com.matching.ezgg.domain.matchInfo.matchKeyword.lane.Lane;
import com.matching.ezgg.domain.riotApi.dto.MatchDto;
import com.matching.ezgg.domain.riotApi.dto.MatchReviewDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
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
			return MatchDto.builder()
				.memberId(memberId)
				.riotMatchId(riotMatchId)
				.kills(kills)
				.deaths(deaths)
				.assists(assists)
				.teamPosition(teamPosition)
				.championName(championName)
				.win(win)
				.build();

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

	public List<MatchReviewDto> toMatchReviewDto(String rawJson) {
		try {
			JsonNode root = objectMapper.readTree(rawJson);
			List<MatchReviewDto> matchReviewDtoList = new ArrayList<>();

			JsonNode participants = root.path("info").path("participants");

			for(JsonNode participant : participants) {

				String riotUsername = participant.path("riotIdGameName").asText();
				String riotTag = participant.path("riotIdTagline").asText();
				int teamId = participant.path("teamId").asInt();

				MatchReviewDto matchReviewDto = MatchReviewDto.builder()
					.riotUsername(riotUsername)
					.riotTag(riotTag)
					.teamId(teamId)
					.build();

				matchReviewDtoList.add(matchReviewDto);
			}

			return matchReviewDtoList;
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("JSON → MathReviewDto 변환 실패", e);
		}
	}

	// participant 배열에서 상대 라인의 노드 리턴
	private JsonNode findOpponentMember(JsonNode participants, String puuid, String teamPosition) {
		for (JsonNode participant : participants) {
			if (!puuid.equals(participant.path("puuid").asText())&&teamPosition.equals(participant.path("teamPosition").asText())) {
				return participant;
			}
		}
		throw new IllegalArgumentException("해당 Match에서 멤버를 찾을 수 없습니다.");
	}

	// participant 배열에서 puuid가 일치하는 노드에서 challenges 노드 리턴
	private JsonNode findChallenges(JsonNode memberNode) {
		JsonNode challengesNode = memberNode.path("challenges");
		if (challengesNode.isMissingNode() || challengesNode.isNull()) {
			throw new IllegalArgumentException("해당 멤버의 Challenges를 찾을 수 없습니다.");
		}
		return challengesNode;
	}



	public GlobalMatchParsingDto toGlobalMatchParsingDto(String rawJson, String puuid) {
		try {
			JsonNode root = objectMapper.readTree(rawJson);

			Integer gameDuration = root.path("info").path("gameDuration").asInt();

			//participants 노드 안에 있는 요소 파싱
			JsonNode globalNode = findTargetMember(root.path("info").path("participants"), puuid);

			Boolean win = globalNode.path("win").asBoolean();
			Boolean gameEndedInSurrender = globalNode.path("gameEndedInSurrender").asBoolean();
			Integer longestTimeSpentLiving = globalNode.path("longestTimeSpentLiving").asInt();

			//challenges 노드 안에 있는 요소 파싱
			JsonNode challengesNode = findChallenges(globalNode);

			Double killParticipation = challengesNode.path("killParticipation").asDouble();
			Double kda = challengesNode.path("kda").asDouble();
			Boolean bestTeamDamagePercentage = Boolean.FALSE;
			Integer maxLevelLeadLaneOpponent = challengesNode.path("maxLevelLeadLaneOpponent").asInt();
			Integer immobilizeAndKillWithAlly = challengesNode.path("immobilizeAndKillWithAlly").asInt();
			Integer multiKillOneSpell = challengesNode.path("multiKillOneSpell").asInt();
			Double damagePerMinute = challengesNode.path("damagePerMinute").asDouble();
			Integer lostAnInhibitor = challengesNode.path("lostAnInhibitor").asInt();
			Integer takedownsBeforeJungleMinionSpawn = challengesNode.path("takedownsBeforeJungleMinionSpawn").asInt();
			Integer pickKillWithAlly = challengesNode.path("pickKillWithAlly").asInt();

			Integer teamId = globalNode.path("teamId").asInt();
			String participantPuuid = "";
			double maxDamage = 0.0;

			JsonNode info = root.get("info");
			JsonNode participants = info.get("participants");

			for (JsonNode participant : participants) {
				int participantTeamId = participant.path("teamId").asInt();

				if (participantTeamId == teamId) {
					JsonNode challenges = participant.path("challenges");
					if (challenges.has("teamDamagePercentage")) {
						double damage = challenges.get("teamDamagePercentage").doubleValue();

						if (damage > maxDamage) {
							maxDamage = damage;
							participantPuuid = participant.path("puuid").asText();
						}
					}
				}
				if (participantPuuid.equals(puuid)) {
					bestTeamDamagePercentage = Boolean.TRUE;
				}
			}


			return GlobalMatchParsingDto.builder()
				.win(win)
				.damagePerMinute(damagePerMinute)
				.gameEndedInSurrender(gameEndedInSurrender)
				.killParticipation(killParticipation)
				.kda(kda)
				.bestTeamDamagePercentage(bestTeamDamagePercentage)
				.maxLevelLeadLaneOpponent(maxLevelLeadLaneOpponent)
				.immobilizeAndKillWithAlly(immobilizeAndKillWithAlly)
				.multiKillOneSpell(multiKillOneSpell)
				.lostAnInhibitor(lostAnInhibitor)
				.takedownsBeforeJungleMinionSpawn(takedownsBeforeJungleMinionSpawn)
				.pickKillWithAlly(pickKillWithAlly)
				.longestTimeSpentLiving(longestTimeSpentLiving)
				.gameDuration(gameDuration)
				.build();

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("JSON → GlobalMatchParsingDto 변환 실패", e);
		}
	}


	public LanerMatchParsingDto toLanerMatchParsingDto(String rawJson, String puuid, String teamPosition) {
		try {
			JsonNode root = objectMapper.readTree(rawJson);

			//participants 노드 안에 있는 요소 파싱
			JsonNode lanerNode = findTargetMember(root.path("info").path("participants"), puuid);
			Integer turretKills = lanerNode.path("turretKills").asInt();
			Integer turretsLost = lanerNode.path("turretsLost").asInt();
			Boolean firstBloodKill = lanerNode.path("firstBloodKill").asBoolean();

			//challenges 노드 안에 있는 요소 파싱
			JsonNode lanerChallengesNode = findChallenges(lanerNode);

			Integer killsOnOtherLanesEarlyJungleAsLaner = lanerChallengesNode.path("killsOnOtherLanesEarlyJungleAsLaner").asInt();
			Integer getTakedownsInAllLanesEarlyJungleAsLaner = lanerChallengesNode.path("getTakedownsInAllLanesEarlyJungleAsLaner").asInt();
			Integer turretPlatesTaken = lanerChallengesNode.path("turretPlatesTaken").asInt();

			Integer killsUnderOwnTurret = lanerChallengesNode.path("killsUnderOwnTurret").asInt();
			Integer killsNearEnemyTurret = lanerChallengesNode.path("killsNearEnemyTurret").asInt();
			Double maxCsAdvantageOnLaneOpponent = lanerChallengesNode.path("maxCsAdvantageOnLaneOpponent").asDouble();
			Integer killAfterHiddenWithAlly = lanerChallengesNode.path("killAfterHiddenWithAlly").asInt();

			//상대 라이너 정보 파싱
			JsonNode opponentLanerNode = findOpponentMember(root.path("info").path("participants"), puuid,
				teamPosition);
			Integer opponentTurretsLost = opponentLanerNode.path("turretsLost").asInt();

			JsonNode opponentLanerChallengesNode = findChallenges(opponentLanerNode);
			Integer opponentTurretPlatesTaken = opponentLanerChallengesNode.path("turretPlatesTaken").asInt();

			return LanerMatchParsingDto.builder()
				.turretKills(turretKills)
				.turretsLost(turretsLost)
				.firstBloodKill(firstBloodKill)
				.killsOnOtherLanesEarlyJungleAsLaner(killsOnOtherLanesEarlyJungleAsLaner)
				.getTakedownsInAllLanesEarlyJungleAsLaner(getTakedownsInAllLanesEarlyJungleAsLaner)
				.turretPlatesTaken(turretPlatesTaken)
				.killsUnderOwnTurret(killsUnderOwnTurret)
				.killsNearEnemyTurret(killsNearEnemyTurret)
				.maxCsAdvantageOnLaneOpponent(maxCsAdvantageOnLaneOpponent)
				.opponentTurretPlatesTaken(opponentTurretPlatesTaken)
				.opponentTurretsLost(opponentTurretsLost)
				.killAfterHiddenWithAlly(killAfterHiddenWithAlly)
				.build();

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("JSON → LanerMatchParsingDto 변환 실패", e);
		}
	}

	public JugMatchParsingDto toJugMatchParsingDto(String rawJson, String puuid) {
		try {
			JsonNode root = objectMapper.readTree(rawJson);

			//participants 노드 안에 있는 요소 파싱
			JsonNode jugNode = findTargetMember(root.path("info").path("participants"), puuid);
			Boolean firstBloodKill = jugNode.path("firstBloodKill").asBoolean();

			//challenges 노드 안에 있는 요소 파싱
			JsonNode jugChallengesNode = findChallenges(jugNode);

			Integer riftHeraldTakedowns = jugChallengesNode.path("riftHeraldTakedowns").asInt();
			Integer dragonTakedowns = jugChallengesNode.path("dragonTakedowns").asInt();
			Integer baronTakedowns = jugChallengesNode.path("baronTakedowns").asInt();

			Double visionScoreAdvantageLaneOpponent = jugChallengesNode.path("visionScoreAdvantageLaneOpponent").doubleValue();
			Integer epicMonsterSteals = jugChallengesNode.path("epicMonsterSteals").asInt();
			Integer enemyJungleMonsterKills = jugChallengesNode.path("enemyJungleMonsterKills").asInt();
			Double moreEnemyJungleThanOpponent = jugChallengesNode.path("moreEnemyJungleThanOpponent").asDouble();
			Integer multiTurretRiftHeraldCount = jugChallengesNode.path("multiTurretRiftHeraldCount").asInt();

			//상대 정글 정보 파싱
			JsonNode opponentJugNode = findOpponentMember(root.path("info").path("participants"), puuid,
				Lane.JUNGLE.name());
			JsonNode opponentJugChallengesNode = findChallenges(opponentJugNode);

			Integer opponentRiftHeraldTakedowns = opponentJugChallengesNode.path("riftHeraldTakedowns").asInt();
			Integer opponentDragonTakedowns = opponentJugChallengesNode.path("dragonTakedowns").asInt();
			Integer opponentBaronTakedowns = opponentJugChallengesNode.path("baronTakedowns").asInt();
			Double opponentMoreEnemyJungleThanOpponent = opponentJugChallengesNode.path("moreEnemyJungleThanOpponent").asDouble();

			return JugMatchParsingDto.builder()
				.visionScoreAdvantageLaneOpponent(visionScoreAdvantageLaneOpponent)
				.epicMonsterSteals(epicMonsterSteals)
				.enemyJungleMonsterKills(enemyJungleMonsterKills)
				.riftHeraldTakedowns(riftHeraldTakedowns)
				.dragonTakedowns(dragonTakedowns)
				.baronTakedowns(baronTakedowns)
				.opponentRiftHeraldTakeDowns(opponentRiftHeraldTakedowns)
				.opponentDragonTakedowns(opponentDragonTakedowns)
				.opponentBaronTakedowns(opponentBaronTakedowns)
				.firstBloodKill(firstBloodKill)
				.moreEnemyJungleThanOpponent(moreEnemyJungleThanOpponent)
				.opponentMoreEnemyJungleThanOpponent(opponentMoreEnemyJungleThanOpponent)
				.multiTurretRiftHeraldCount(multiTurretRiftHeraldCount)
				.build();

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("JSON → JugMatchParsingDto 변환 실패", e);
		}
	}

	public SupMatchParsingDto toSupMatchParsingDto(String rawJson, String puuid) {
		try {
			JsonNode root = objectMapper.readTree(rawJson);

			//participants 노드 안에 있는 요소 파싱
			JsonNode supNode = findTargetMember(root.path("info").path("participants"), puuid);
			Integer wardsPlaced = supNode.path("wardsPlaced").asInt();
			Integer assists = supNode.path("assists").asInt();

			//challenges 노드 안에 있는 요소 파싱
			JsonNode supChallengesNode = findChallenges(supNode);

			Double visionScoreAdvantageLaneOpponent = supChallengesNode.path("visionScoreAdvantageLaneOpponent").asDouble();
			Integer saveAllyFromDeath = supChallengesNode.path("saveAllyFromDeath").asInt();

			Integer gameDuration = root.path("info").path("gameDuration").asInt();

			return SupMatchParsingDto.builder()
				.wardPlaced(wardsPlaced)
				.assists(assists)
				.visionScoreAdvantageLaneOpponent(visionScoreAdvantageLaneOpponent)
				.saveAllyFromDeath(saveAllyFromDeath)
				.gameDuration(gameDuration)
				.build();

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("JSON → SupMatchParsingDto 변환 실패", e);
		}
	}
}
