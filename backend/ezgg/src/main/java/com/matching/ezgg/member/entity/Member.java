package com.matching.ezgg.member.entity;

import com.matching.ezgg.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String memberUsername;

	@Column(nullable = false)
	private String password;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(unique = true, nullable = false)
	private String riotUsername;

	@Column(unique = true, nullable = false)
	private String riotTag;

	private String role;

	@Builder
	public Member(Long id, String memberUsername, String password, String email, String riotUsername, String riotTag,
		String role) {
		this.id = id;
		this.memberUsername = memberUsername;
		this.password = password;
		this.email = email;
		this.riotUsername = riotUsername;
		this.riotTag = riotTag;
		this.role = role;
	}
}
