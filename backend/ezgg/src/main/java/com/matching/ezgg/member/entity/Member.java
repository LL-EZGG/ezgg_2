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
	private Long memberNo;

	@Column(unique = true, nullable = false)
	private String memberId;

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
	public Member(Long memberNo, String memberId, String password, String email, String riotUsername, String riotTag,
		String role) {
		this.memberNo = memberNo;
		this.memberId = memberId;
		this.password = password;
		this.email = email;
		this.riotUsername = riotUsername;
		this.riotTag = riotTag;
		this.role = role;
	}
}
