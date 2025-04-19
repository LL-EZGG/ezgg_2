package com.matching.ezgg.api.dto;

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
public class WinRateNTierDto {
	private String queueType;

	private String puuid;
	private Integer wins;
	private Integer losses;
	private String tier;
	private String rank;
}
