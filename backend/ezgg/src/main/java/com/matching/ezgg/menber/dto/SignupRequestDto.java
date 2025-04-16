package com.matching.ezgg.menber.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignupRequestDto {

	private String memberId;
	private String password;
	private String email;
	private String riotUsername;
	private String riotTag;

	@Builder
	public SignupRequestDto(String memberId, String password, String email, String riotUsername, String riotTag) {
		this.memberId = memberId;
		this.password = password;
		this.email = email;
		this.riotUsername = riotUsername;
		this.riotTag = riotTag;
	}
}
