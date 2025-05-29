package com.matching.ezgg.domain.matchInfo.matchKeyword.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matchInfo.matchKeyword.championInfo.ChampionRole;
import com.matching.ezgg.domain.matchInfo.matchKeyword.dto.analysis.Analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChampionInfoService {

	/**
	 * 챔피언 역할 등급을 계산하는 메서드
	 * @param championRoleCount
	 * @return 챔피언 역할 등급 String
	 */

	private String evaluateChampionRole(int championRoleCount, int lanePlayCount) {
		if (championRoleCount == 0) {
			return "없음";
		}
		if (lanePlayCount <= 3) { //해당 라인을 3회 이하로 플레이한 경우
			return "보통";
		}
		double ratio = (double) championRoleCount / lanePlayCount;
		return ratio >= 0.4 ? "좋음" : "보통";
	}

	/**
	 * 챔피언명에서 특수문자 및 공백 제거하는 메서드
	 * @param championName
	 * @return 챔피언명 String
	 */

	public String cleanedName(String championName) {
		return championName
			.replaceAll("[^a-zA-Z0-9]", "")
			.toUpperCase();
	}

	/**
	 * 라인별 챔피언 역할 등급 계산하는 메서드
	 * @param championRoleCounts
	 * @param lanePlayCount
	 * @param analysis
	 */

	public void evaluateChampionRolesForLane(Map<ChampionRole, Integer> championRoleCounts, int lanePlayCount, Analysis<? extends Enum<?>> analysis) {
		if (championRoleCounts == null) return;

		for (Map.Entry<ChampionRole, Integer> entry : championRoleCounts.entrySet()) {
			ChampionRole role = entry.getKey();
			int count = entry.getValue();
			analysis.getChampionRole().put(role.name(), evaluateChampionRole(count, lanePlayCount));
		}
	}
}
