package com.matching.ezgg.matching.dto;

import com.matching.ezgg.data.memberInfo.entity.MemberInfo;
import com.matching.ezgg.data.recentTwentyMatch.entity.RecentTwentyMatch;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDataBundle {
	private MemberInfo memberInfo;
	private RecentTwentyMatch recentTwentyMatch;

	@Builder
	public MemberDataBundle(MemberInfo memberInfo, RecentTwentyMatch recentTwentyMatch){
		this.memberInfo = memberInfo;
		this.recentTwentyMatch = recentTwentyMatch; // 한판도 안한 경우 빈 객체가 나옴
	}
}
