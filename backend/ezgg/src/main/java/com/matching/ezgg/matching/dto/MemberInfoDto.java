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
public class MemberInfoDto {
	private String riotUsername;
	private String riotTag;
	private String tier;
	private String tierNum;
	private int wins;
	private int losses;

	// 	"memberInfo" : {
	// 		"riotUsername" : "RiotUsername",
	// 		"riotTag" : "RiotTag",
	// 		"sessionInfo" : {
	// 			"tier" : "Gold",
	// 			"tierNum" : "IV",
	// 			"wins" : 10,
	// 			"losses" : 5,
	// 			"winRate" : 15,
	// 		}
	// 	},
}
