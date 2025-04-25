package com.matching.ezgg.matching.dto;

import java.util.List;

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
public class RecentTwentyMatchParsingDto {

	private int kills;
	private int deaths;
	private int assists;
	private List<MostChampion> mostChampions;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@ToString
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
}
