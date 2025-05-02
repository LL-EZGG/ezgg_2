package com.matching.ezgg.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WinRateNTierDto {
	private String queueType;

	private String puuid;
	private Integer wins;
	private Integer losses;
	private String tier;
	@JsonProperty("rank")
	private String tierNum;


	public static WinRateNTierDto unranked(String puuid) {
		return new WinRateNTierDto("RANKED_SOLO_5x5", puuid, 0, 0, "unranked", "unranked");
	}

}
