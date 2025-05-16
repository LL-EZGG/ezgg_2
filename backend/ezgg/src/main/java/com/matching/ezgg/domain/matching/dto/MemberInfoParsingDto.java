package com.matching.ezgg.domain.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberInfoParsingDto {
	private String riotUsername;
	private String riotTag;
	private String tier;
	private String tierNum;
	private int wins;
	private int losses;
}
