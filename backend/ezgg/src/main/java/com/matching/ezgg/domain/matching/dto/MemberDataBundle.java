package com.matching.ezgg.domain.matching.dto;

import com.matching.ezgg.domain.member.dto.MemberInfoDto;
import com.matching.ezgg.domain.recentTwentyMatch.dto.RecentTwentyMatchDto;

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
public class MemberDataBundle {
	private MemberInfoDto memberInfoDto;
	private RecentTwentyMatchDto recentTwentyMatchDto;

	public MemberDataBundle toDto(MemberInfoDto memberInfoDto, RecentTwentyMatchDto recentTwentyMatchDto) {
		return MemberDataBundle.builder()
			.memberInfoDto(memberInfoDto)
			.recentTwentyMatchDto(recentTwentyMatchDto) // 한판도 안한 경우 빈 객체가 나옴
			.build();
	}
}
