package com.matching.ezgg.api.domain.memberinfo.entity;

import java.util.List;

import com.matching.ezgg.common.MatchIdConvert;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String puuid;

	@Convert(converter = MatchIdConvert.class)
	private List<String> matchIds;
	private Long wins;
	private Long losses;

	@Builder
	public MemberInfo(String puuid, List<String> matchIds, Long wins, Long losses) {
		this.puuid = puuid;
		this.matchIds = matchIds;
		this.wins = wins;
		this.losses = losses;
	}
}
