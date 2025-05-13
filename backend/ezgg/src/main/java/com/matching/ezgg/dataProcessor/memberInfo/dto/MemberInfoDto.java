package com.matching.ezgg.dataProcessor.memberInfo.dto;

import com.matching.ezgg.dataProcessor.memberInfo.entity.MemberInfo;

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

	public static MemberInfoDto toDto(MemberInfo memberInfo) {
		return MemberInfoDto.builder()
			.riotUsername(memberInfo.getRiotUsername())
			.riotTag(memberInfo.getRiotTag())
			.tier(memberInfo.getTier())
			.tierNum(memberInfo.getTierNum())
			.wins(memberInfo.getWins())
			.losses(memberInfo.getLosses())
			.build();
	}
}
