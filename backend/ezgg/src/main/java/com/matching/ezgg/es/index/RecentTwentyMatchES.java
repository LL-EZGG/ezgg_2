package com.matching.ezgg.es.index;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
public class RecentTwentyMatchES {

	@Field(type = FieldType.Object)
	private KDA sumKda;

	@Field(type = FieldType.Object)
	private List<ChampionsStats> mostChampions;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class KDA {
		private int kills;
		private int deaths;
		private int assists;
	}

	@Getter
	@Setter
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
}
