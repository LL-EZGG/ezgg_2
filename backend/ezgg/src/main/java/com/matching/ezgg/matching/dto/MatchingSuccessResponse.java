package com.matching.ezgg.matching.dto;

import com.matching.ezgg.data.memberInfo.dto.MemberInfoDto;
import com.matching.ezgg.data.recentTwentyMatch.dto.RecentTwentyMatchDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MatchingSuccessResponse {
	private String status;
	private MatchedMemberData data;

	@Getter
	@AllArgsConstructor
	@Builder
	public static class MatchedMemberData {
		private Long matchedMemberId;
		private MemberInfoDto memberInfoDto;
		private RecentTwentyMatchDto recentTwentyMatchDto;
	}
}
