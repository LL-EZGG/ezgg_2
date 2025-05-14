package com.matching.ezgg.domain.matching.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PreferredPartnerParsingDto {

	private WantLine wantLine;

	@JsonProperty("selectedChampions")
	private ChampionInfo championInfo;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ToString
	public static class WantLine {
		private String myLine;
		private String partnerLine;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ToString
	public static class ChampionInfo {
		private List<String> preferredChampions;
		@JsonProperty("bannedChampions")
		private List<String> unpreferredChampions;
	}
}
