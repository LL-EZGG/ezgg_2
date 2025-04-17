package com.matching.ezgg.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

	private String memberId;
	@Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "패스워드 4~20자의 영문 대소문자와 숫자만 사용 가능합니다.")
	private String password;
	private String email;
	private String riotUsername;
	private String riotTag;

	@Builder
	public SignupRequest(String memberId, String password, String email, String riotUsername, String riotTag) {
		this.memberId = memberId;
		this.password = password;
		this.email = email;
		this.riotUsername = riotUsername;
		this.riotTag = riotTag;
	}
}
