package com.matching.ezgg.domain.member.dto;

import com.matching.ezgg.domain.memberInfo.entity.MemberInfo;

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
	private Integer wins;
	private Integer losses;

	public static MemberInfoDto toDto(MemberInfo memberInfo) {
		return MemberInfoDto.builder()
			.riotUsername(memberInfo.getRiotUsername())
			.riotTag(memberInfo.getRiotTag())
			.tier(memberInfo.getTier())
			.wins(memberInfo.getWins())
			.losses(memberInfo.getLosses())
			.build();
	}
}
