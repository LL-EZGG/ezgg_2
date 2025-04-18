package com.matching.ezgg.global.jwt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Refresh {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String memberId;
	private String refresh;
	private String expiration;

	@Builder
	public Refresh(String memberId, String refresh, String expiration) {
		this.memberId = memberId;
		this.refresh = refresh;
		this.expiration = expiration;
	}
}
