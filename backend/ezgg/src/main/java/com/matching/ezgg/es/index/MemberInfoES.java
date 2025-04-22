package com.matching.ezgg.es.index;

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
public class MemberInfoES {
	private String riotUsername;
	private String riotTag;
	private Long memberId;

	@Field(type = FieldType.Object)
	private SeasonInfo seasonInfo;


	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SeasonInfo {
		private String tier;
		private String rank;
		private int wins;
		private int defeats;
		private int winRate;
	}
}
