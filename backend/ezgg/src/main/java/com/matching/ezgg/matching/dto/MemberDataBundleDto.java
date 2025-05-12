package com.matching.ezgg.matching.dto;

import com.matching.ezgg.data.memberInfo.dto.MemberInfoDto;
import com.matching.ezgg.data.recentTwentyMatch.dto.RecentTwentyMatchDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberDataBundleDto {
	private MemberInfoDto memberInfoDto;
	private RecentTwentyMatchDto recentTwentyMatchDto;

	public static MemberDataBundleDto toDto(MemberInfoDto memberInfoDto, RecentTwentyMatchDto recentTwentyMatchDto) {
		return MemberDataBundleDto.builder()
			.memberInfoDto(memberInfoDto)
			.recentTwentyMatchDto(recentTwentyMatchDto)
			.build();
	}
}
