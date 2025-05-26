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
public class UserProfile {

	private String riotUsername;
	private String riotTag;
	private String tier;

	@Field(type = FieldType.Object)
	private recentTwentyMatchStats recentTwentyMatchStats;

	private String reviewScore;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class recentTwentyMatchStats {
		private List<String> most3Champions;
		private float[] topAnalysisVector;
		private float[] jugAnalysisVector;
		private float[] midAnalysisVector;
		private float[] adAnalysisVector;
		private float[] supAnalysisVector;
	}
}
