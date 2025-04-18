package com.matching.ezgg.api.domain.match.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MatchDto {//TODO 예시DTO. 정제한 데이터 형태에 따라 조정해주세요
	// private long memberId;
	private String riotMatchId;
	private int kills;
	private int deaths;
	private int assists;
	private String teamPosition;
	private String championName;
	private boolean win;
}
