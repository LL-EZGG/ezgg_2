package com.matching.ezgg.domain.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MatchingFilterParsingDto {

	private Long memberId;
	private PreferredPartnerParsingDto preferredPartnerParsing;
	private MemberInfoParsingDto memberInfoParsing;
	private RecentTwentyMatchParsingDto recentTwentyMatchParsing;
	private int matchingScore;

}
