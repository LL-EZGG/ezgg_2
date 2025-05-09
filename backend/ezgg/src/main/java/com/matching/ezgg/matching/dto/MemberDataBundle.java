package com.matching.ezgg.matching.dto;

import com.matching.ezgg.data.recentTwentyMatch.dto.RecentTwentyMatchDto;
import com.matching.ezgg.member.dto.MemberInfoDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDataBundle {
	private MemberInfoDto memberInfoDto;
	private RecentTwentyMatchDto recentTwentyMatchDto;

	@Builder
	public MemberDataBundle(MemberInfoDto memberInfoDto, RecentTwentyMatchDto recentTwentyMatchDto){
		this.memberInfoDto = memberInfoDto;
		this.recentTwentyMatchDto = recentTwentyMatchDto; // 한판도 안한 경우 빈 객체가 나옴
	}
}
