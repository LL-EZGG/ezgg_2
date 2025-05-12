package com.matching.ezgg.matchCore.memberInfo.dto;

import java.util.List;

import com.matching.ezgg.matchCore.memberInfo.entity.MemberInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoDto {
	private String riotUsername;
	private String riotTag;
	private String tier;
	private String tierNum;
	private Integer wins;
	private Integer losses;
	private List<String> matchIds;
	private Long memberId;

	public static MemberInfoDto toDto(MemberInfo memberInfo) {
		return MemberInfoDto.builder()
			.riotUsername(memberInfo.getRiotUsername())
			.riotTag(memberInfo.getRiotTag())
			.tier(memberInfo.getTier())
			.tierNum(memberInfo.getTierNum())
			.wins(memberInfo.getWins())
			.losses(memberInfo.getLosses())
			.matchIds(memberInfo.getMatchIds())
			.memberId(memberInfo.getMemberId())
			.build();
	}
}
