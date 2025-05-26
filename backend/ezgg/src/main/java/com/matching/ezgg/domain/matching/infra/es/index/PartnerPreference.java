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
public class PartnerPreference {

	private float[] userPreferenceTextVector;

	@Field(type = FieldType.Object)
	private WantLine wantLine;

	@Field(type = FieldType.Object)
	private ChampionsInfo championsInfo;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class WantLine {
		private String myLine;
		private String partnerLine;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ChampionsInfo {
		private List<String> preferredChampion;
		private List<String> unPreferredChampion;
	}
}
