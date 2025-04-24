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
public class MatchingFilterDto {

	private Long memberId;
	private PreferredPartnerDto preferredPartner;
	private MemberInfoDto memberInfo;
	private RecentTwentyMath recentTwentyMatch;
	private int matchingScore;

	// {
	// 	"memberId" : "1",
	// 	"preferredPartner" : {
	// 		"wantLine" : {
	// 			"myLine" : "top",
	// 			"partnerLine" : "mid"
	// 		},
	// 		"championInfo" : {
	// 			"preferredChampion" : "Aatrox",
	// 			"unpreferredChampion" : "Zed"
	// 		}
	// 	},
	// 	"memberInfo" : {
	// 		"riotUsername" : "RiotUsername",
	// 		"riotTag" : "RiotTag",
	// 		"tier" : "Gold",
	// 		"tierNum" : "IV",
	// 		"wins" : 10,
	// 		"losses" : 5,
	// 	},
	// 	"recentTwentyMatch" : {
	// 		"sumKills" : 100,
	// 		"sumDeaths" : 50,
	// 		"sumAssists" : 70,
	// 		"mostChampion" : [
	// 			{
	// 				"championName" : "Aatrox",
	// 				"kills" : 10,
	// 				"deaths" : 5,
	// 				"assists" : 7,
	// 				"wins" : 20,
	// 				"losses" : 10,
	// 				"totalMatches" : 30,
	// 				"winRateOfChampion" : 66
	// 			},
	// 			{
	// 				"championName" : "Zed",
	// 				"kills" : 15,
	// 				"deaths" : 3,
	// 				"assists" : 5,
	// 				"wins" : 25,
	// 				"losses" : 5,
	// 				"totalMatches" : 30,
	// 				"winRateOfChampion" : 83
	// 			},
	// 			{
	// 				"championName" : "Lee Sin",
	// 				"kills" : 8,
	// 				"deaths" : 2,
	// 				"assists" : 10,
	// 				"wins" : 15,
	// 				"losses" : 5,
	// 				"totalMatches" : 20,
	// 				"winRateOfChampion" : 75
	// 			}
	// 		]
	// 	}
	// }
}
