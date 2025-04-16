package com.matching.ezgg.menber.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupResponseDto {

	private String message;

	@Builder
	public SignupResponseDto(String message) {
		this.message = message;
	}
}
