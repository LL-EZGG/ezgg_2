package com.matching.ezgg.api.domain.match.entity;

import com.matching.ezgg.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_info")
public class Match extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "member_id", unique = false, nullable = false)
	private Long memberId;

	@Column(name = "riot_match_id", unique = false, nullable = false)
	private String riotMatchId;

	@Column(name = "kills", unique = false, nullable = false)
	private Integer kills;

	@Column(name = "deaths", unique = false, nullable = false)
	private Integer deaths;

	@Column(name = "assists", unique = false, nullable = false)
	private Integer assists;

	@Column(name = "team_position", unique = false, nullable = false)
	private String teamPosition;

	@Column(name = "champion_name", unique = false, nullable = false)
	private String championName;

	@Column(name = "win", unique = false, nullable = false)
	private Boolean win;

	@Builder
	public Match(Long memberId, String riotMatchId, Integer kills, Integer deaths, Integer assists, String teamPosition,
		String championName, Boolean win) {
		this.memberId = memberId;
		this.riotMatchId = riotMatchId;
		this.kills = kills;
		this.deaths = deaths;
		this.assists = assists;
		this.teamPosition = teamPosition;
		this.championName = championName;
		this.win = win;
	}
}
