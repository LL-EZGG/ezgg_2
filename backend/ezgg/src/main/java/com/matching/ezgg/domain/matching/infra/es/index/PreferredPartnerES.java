package com.matching.ezgg.domain.matching.infra.es.index;

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
public class PreferredPartnerES {

	private String tier;

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
		private String preferredChampion;
		private String unpreferredChampion;
	}

}
