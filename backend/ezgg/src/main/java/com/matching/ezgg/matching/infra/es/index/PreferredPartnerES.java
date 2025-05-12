package com.matching.ezgg.matching.infra.es.index;

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
public class PreferredPartnerES {

	private String tier;

	@Field(type = FieldType.Object)
	private WantLine wantLine;

	@Field(type = FieldType.Object)
	private ChampionsInfo championsInfo;

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
	public static class ChampionsInfo {
		private String preferredChampion;
		private String unpreferredChampion;
	}

}
