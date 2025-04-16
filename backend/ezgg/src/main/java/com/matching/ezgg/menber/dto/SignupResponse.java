package com.matching.ezgg.menber.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupResponse {

	private String memberId;
	private String email;
	private String riotUsername;
	private String riotTag;

	@Builder
	public SignupResponse(String memberId, String email, String riotUsername, String riotTag) {
		this.memberId = memberId;
		this.email = email;
		this.riotUsername = riotUsername;
		this.riotTag = riotTag;
	}
}
