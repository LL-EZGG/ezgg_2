package com.matching.ezgg.domain.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) // Builder를 사용하여 객체를 생성할 때, 기존 객체를 기반으로 새로운 객체를 생성할 수 있도록 설정
@ToString
public class MatchingFilterParsingDto {

	private Long memberId;
	private PreferredPartnerParsingDto preferredPartnerParsing;
	private MemberInfoParsingDto memberInfoParsing;
	private RecentTwentyMatchParsingDto recentTwentyMatchParsing;
	private int matchingScore;

}
