package com.matching.ezgg.domain.matching.dto;

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
		private String preferredChampion;
		private String unpreferredChampion;
	}
}
