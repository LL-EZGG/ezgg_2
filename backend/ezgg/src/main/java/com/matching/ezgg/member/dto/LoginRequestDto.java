package com.matching.ezgg.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {

	private String memberId;
	private String password;

	public LoginRequestDto(String memberId, String password) {
		this.memberId = memberId;
		this.password = password;
	}
}
