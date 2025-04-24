package com.matching.ezgg.es.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestDto {

	@JsonProperty("preferred_partner")
	private PreferredPartner preferredPartner;

	@JsonProperty("member_info")
	private MemberInfo memberInfo;

	public static class PreferredPartner {
		@JsonProperty("wantLine")
		private WantLine wantLine;
	}

	public static class WantLine {
		@JsonProperty("myLine")
		private String myLine;

		@JsonProperty("partnerLine")
		private String partnerLine;
	}

	public static class MemberInfo {
		@JsonProperty("riot_username")
		private String riotUsername;

		@JsonProperty("riot_tag")
		private String riotTag;

		@JsonProperty("member_id")
		private int memberId;

		@JsonProperty("season_infos")
		private List<SeasonInfo> seasonInfos;
	}

	public static class SeasonInfo {
		@JsonProperty("tier")
		private String tier;
	}
}
