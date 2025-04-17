package com.matching.ezgg.api.domain.match.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match")
public class Match extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;


	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "id", nullable = false)
	// @ToString.Exclude  // 순환 참조 방지
	// @EqualsAndHashCode.Exclude // 순환 참조 방지
	// private Member member;TODO

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
	public Match(/**Long memberId,**/String riotMatchId, Integer kills, Integer deaths, Integer assists, String teamPosition, String championName, Boolean win) {
		/**this.memberId = memberId;**///TODO
		this.riotMatchId = riotMatchId;
		this.kills = kills;
		this.deaths = deaths;
		this.assists = assists;
		this.teamPosition = teamPosition;
		this.championName = championName;
		this.win = win;
	}
}
