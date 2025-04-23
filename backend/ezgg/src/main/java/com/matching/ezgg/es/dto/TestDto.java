package com.matching.ezgg.es.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestDto {

	@JsonProperty("preferred_partner")
	private PreferredPartner preferredPartner;

	@JsonProperty("member_info")
	private MemberInfo memberInfo;

	public PreferredPartner getPreferredPartner() {
		return preferredPartner;
	}

	public void setPreferredPartner(PreferredPartner preferredPartner) {
		this.preferredPartner = preferredPartner;
	}

	public MemberInfo getMemberInfo() {
		return memberInfo;
	}

	public void setMemberInfo(MemberInfo memberInfo) {
		this.memberInfo = memberInfo;
	}

	public static class PreferredPartner {
		@JsonProperty("wantLine")
		private WantLine wantLine;

		public WantLine getWantLine() {
			return wantLine;
		}

		public void setWantLine(WantLine wantLine) {
			this.wantLine = wantLine;
		}
	}

	public static class WantLine {
		@JsonProperty("myLine")
		private String myLine;

		@JsonProperty("partnerLine")
		private String partnerLine;

		public String getMyLine() {
			return myLine;
		}

		public void setMyLine(String myLine) {
			this.myLine = myLine;
		}

		public String getPartnerLine() {
			return partnerLine;
		}

		public void setPartnerLine(String partnerLine) {
			this.partnerLine = partnerLine;
		}
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

		public String getRiotUsername() {
			return riotUsername;
		}

		public void setRiotUsername(String riotUsername) {
			this.riotUsername = riotUsername;
		}

		public String getRiotTag() {
			return riotTag;
		}

		public void setRiotTag(String riotTag) {
			this.riotTag = riotTag;
		}

		public int getMemberId() {
			return memberId;
		}

		public void setMemberId(int memberId) {
			this.memberId = memberId;
		}

		public List<SeasonInfo> getSeasonInfos() {
			return seasonInfos;
		}

		public void setSeasonInfos(List<SeasonInfo> seasonInfos) {
			this.seasonInfos = seasonInfos;
		}
	}

	public static class SeasonInfo {
		@JsonProperty("tier")
		private String tier;

		public String getTier() {
			return tier;
		}

		public void setTier(String tier) {
			this.tier = tier;
		}
	}
}
