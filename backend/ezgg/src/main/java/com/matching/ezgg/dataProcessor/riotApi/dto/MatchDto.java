package com.matching.ezgg.dataProcessor.riotApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchDto {
	private long memberId;
	private String riotMatchId;
	private int kills;
	private int deaths;
	private int assists;
	private String teamPosition;
	private String championName;
	private boolean win;
}
