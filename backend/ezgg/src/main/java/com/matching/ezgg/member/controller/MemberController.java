package com.matching.ezgg.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.matching.dto.MemberDataBundleDto;
import com.matching.ezgg.member.dto.SignupRequest;
import com.matching.ezgg.member.dto.SignupResponse;
import com.matching.ezgg.member.service.MemberService;
import com.matching.ezgg.dataProcessor.memberInfo.entity.MemberInfo;
import com.matching.ezgg.matching.service.MemberDataBundleService;
import com.matching.ezgg.dataProcessor.memberInfo.service.MemberInfoService;
import com.matching.ezgg.global.annotation.LoginUser;
import com.matching.ezgg.member.jwt.filter.JWTUtil;
import com.matching.ezgg.member.jwt.repository.RedisRefreshTokenRepository;
import com.matching.ezgg.global.response.SuccessResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final RedisRefreshTokenRepository redisRefreshTokenRepository;
	private final MemberDataBundleService memberDataBundleService;
	private final MemberInfoService memberInfoService;
	private final JWTUtil jwtUtil;

	//회원 가입
	@PostMapping("/signup")
	public ResponseEntity<SuccessResponse<SignupResponse>> signup(
		@Valid @RequestBody SignupRequest signupRequest
	) {
		SignupResponse signupResponse = memberService.signup(signupRequest);

		return ResponseEntity.ok(SuccessResponse.<SignupResponse>builder()
			.code("200")
			.message("회원가입 성공")
			.data(signupResponse)
			.build());
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<SuccessResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
		// Access Token 처리
		String accessToken = jwtUtil.extractTokenFromRequest(request);
		if (accessToken != null && !jwtUtil.isExpired(accessToken)) {
			try {
				// 토큰의 남은 유효시간 계산
				long expirationTime = jwtUtil.getExpirationTime(accessToken);
				long currentTime = System.currentTimeMillis();
				long remainingTime = expirationTime - currentTime;

				if (remainingTime > 0) {
					// Access Token을 블랙리스트에 추가
					redisRefreshTokenRepository.addToBlacklist(accessToken, remainingTime);
					log.info(">>>>> Access Token을 블랙리스트에 추가했습니다.");
				}
			} catch (Exception e) {
				log.error(">>>>> Access Token 블랙리스트 추가 실패: {}", e.getMessage());
			}
		}

		// Refresh Token 처리
		String refreshToken = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("Refresh")) {
					refreshToken = cookie.getValue();
				}
			}
		}

		if (refreshToken != null && jwtUtil.getCategory(refreshToken).equals("refresh")) {
			// 유효한 리프레시 토큰이 있으면 사용자 정보를 가져와서 Redis에서 해당 토큰 삭제
			String memberUsername = jwtUtil.getMemberUsername(refreshToken);
			redisRefreshTokenRepository.deleteByMemberId(memberUsername);
			log.info(">>>>> Redis에서 리프레시 토큰 삭제 완료: {}", memberUsername);
		}

		// 클라이언트 쿠키 삭제
		Cookie refreshCookie = new Cookie("Refresh", null);
		refreshCookie.setMaxAge(0); // 쿠키 즉시 만료
		refreshCookie.setHttpOnly(true);
		response.addCookie(refreshCookie);

		return ResponseEntity.ok(SuccessResponse.<Void>builder()
			.code("200")
			.message("로그아웃 성공")
			.build());
	}

	@GetMapping("/memberinfo")
	public ResponseEntity<SuccessResponse<MemberInfo>> getMemberInfo(@LoginUser Long memberId) {
		// 로그인한 사용자의 ID를 사용하여 회원 정보를 조회
		MemberInfo loggedMemberInfo = memberInfoService.getMemberInfoByMemberId(memberId);

		return ResponseEntity.ok(SuccessResponse.<MemberInfo>builder()
			.code("200")
			.message("회원정보 조회 성공")
			.data(loggedMemberInfo)
			.build());
	}

	@GetMapping("/memberdatabundle")
	public ResponseEntity<SuccessResponse<MemberDataBundleDto>> getMemberDataBundle(@LoginUser Long memberId) {
		// 로그인한 사용자의 ID를 사용하여 회원 정보를 조회
		MemberDataBundleDto loggedMemberDataBundleDto = memberDataBundleService.getMemberDataBundleByMemberId(memberId);

		return ResponseEntity.ok(SuccessResponse.<MemberDataBundleDto>builder()
			.code("200")
			.message("회원정보 조회 성공")
			.data(loggedMemberDataBundleDto)
			.build());
	}
}
