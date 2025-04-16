package com.matching.ezgg.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.global.response.SuccessResponse;
import com.matching.ezgg.member.dto.SignupRequest;
import com.matching.ezgg.member.dto.SignupResponse;
import com.matching.ezgg.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	//회원 가입
	@PostMapping("/signup")
	public ResponseEntity<SuccessResponse<SignupResponse>> signup(
		@RequestBody SignupRequest signupRequest
	) {
		SignupResponse signupResponse = memberService.signup(signupRequest);

		return ResponseEntity.ok(SuccessResponse.<SignupResponse>builder()
			.code("200")
			.message("회원가입 성공")
			.data(signupResponse)
			.build());
	}
	// 로그아웃

}
