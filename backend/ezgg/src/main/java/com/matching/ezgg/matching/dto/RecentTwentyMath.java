package com.matching.ezgg.matching.dto;

import java.util.List;

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
public class RecentTwentyMath {

	private int kills;
	private int deaths;
	private int assists;
	private List<MostChampion> mostChampions;


	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class MostChampion {
		private String championName;
		private int kills;
		private int deaths;
		private int assists;
		private int wins;
		private int losses;
		private int totalMatches;
		private int winRateOfChampion;
	}

	// 	"recentTwentyMatch" : {
	// 		"sumKda" : {
	// 			"kills" : 100,
	// 			"deaths" : 50,
	// 			"assists" : 70,
	// 		},
	// 		"mostChampion" : [
	// 			{
	// 				"championName" : "Aatrox",
	// 				"kills" : 10,
	// 				"deaths" : 5,
	// 				"assists" : 7,
	// 				"wins" : 20,
	// 				"losses" : 10,
	// 				"totalMatches" : 30,
	// 			},
	// 			{
	// 				"championName" : "Zed",
	// 				"kills" : 15,
	// 				"deaths" : 3,
	// 				"assists" : 5,
	// 				"wins" : 25,
	// 				"losses" : 5,
	// 				"totalMatches" : 30,
	// 			},
	// 			{
	// 				"championName" : "Lee Sin",
	// 				"kills" : 8,
	// 				"deaths" : 2,
	// 				"assists" : 10,
	// 				"wins" : 15,
	// 				"losses" : 5,
	// 				"totalMatches" : 20,
	// 			}
	// 		]
	// 	}
}
