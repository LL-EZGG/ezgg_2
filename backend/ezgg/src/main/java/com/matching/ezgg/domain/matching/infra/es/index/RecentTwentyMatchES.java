package com.matching.ezgg.domain.matching.infra.es.index;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentTwentyMatchES {

	@Field(type = FieldType.Object)
	private KDA sumKda;

	@Field(type = FieldType.Object)
	private List<ChampionsStats> mostChampions;

	@Field(type = FieldType.Object)
	private MatchAnalysis matchAnalysis;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class KDA {
		private int kills;
		private int deaths;
		private int assists;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ChampionsStats {
		private String championName;
		private int kills;
		private int deaths;
		private int assists;
		private int wins;
		private int defeats;
		private int totalMatches;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class MatchAnalysis {
		private String topAnalysis;
		private String jugAnalysis;
		private String midAnalysis;
		private String adAnalysis;
		private String supAnalysis;
	}
}
