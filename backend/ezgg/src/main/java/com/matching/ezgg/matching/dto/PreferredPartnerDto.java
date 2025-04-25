package com.matching.ezgg.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferredPartnerDto {

	private WantLine wantLine;
	private ChampionInfo championInfo;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class WantLine {
		private String myLine;
		private String partnerLine;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ChampionInfo {
		private String preferredChampion;
		private String unpreferredChampion;
	}

	// "preferredPartner" : {
	// 	"wantLine" : {
	// 		"myLine" : "top",
	// 		"partnerLine" : "mid"
	// 	},
	// 	"championInfo" : {
	// 		"preferredChampion" : "Aatrox",
	// 		"unpreferredChampion" : "Zed"
	// 	}
	// },
}
