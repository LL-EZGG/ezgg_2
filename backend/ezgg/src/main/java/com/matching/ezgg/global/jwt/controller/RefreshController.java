package com.matching.ezgg.global.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.global.exception.InvalidTokenException;
import com.matching.ezgg.global.jwt.filter.JWTUtil;
import com.matching.ezgg.global.jwt.repository.RedisRefreshTokenRepository;
import com.matching.ezgg.global.response.SuccessResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshController {

	private final JWTUtil jwtUtil;
	private final RedisRefreshTokenRepository redisRefreshTokenRepository;

	@PostMapping("/refresh")
	public ResponseEntity<SuccessResponse<Void>> refresh(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = null;

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("Refresh")) {
					refreshToken = cookie.getValue();
				}
			}
		}

		if (refreshToken == null) {
			log.info(">>>>> refresh-token이 없습니다.");
			throw new InvalidTokenException();
		}

		if (!jwtUtil.getCategory(refreshToken).equals("refresh")) {
			log.error(">>>>> refresh-token이 아닙니다.");
			throw new InvalidTokenException();
		}

		String memberUsername = jwtUtil.getMemberUsername(refreshToken);
		
		// Redis에서 저장된 리프레시 토큰 조회
		String savedRefreshToken = redisRefreshTokenRepository.findByMemberId(memberUsername);
		
		if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
			log.error(">>>>> 존재하지 않는 refresh-token입니다.");
			throw new InvalidTokenException();
		}

		String role = jwtUtil.getRole(refreshToken);

		long accessTokenExpiry = 60 * 60 * 1000L; // 1시간 유효 
		long refreshTokenExpiry = 24 * 60 * 60 * 1000L; // 1일 유효

		String newAccessToken = jwtUtil.createJwt("access", memberUsername, role, accessTokenExpiry);
		String newRefreshToken = jwtUtil.createJwt("refresh", memberUsername, role, refreshTokenExpiry);

		// Redis에서 기존 토큰 삭제 후 새 토큰 저장
		redisRefreshTokenRepository.deleteByMemberId(memberUsername);
		redisRefreshTokenRepository.save(memberUsername, newRefreshToken, refreshTokenExpiry);

		response.setHeader("Authorization", newAccessToken);
		response.addCookie(createCookie("Refresh", newRefreshToken));

		return ResponseEntity.ok(SuccessResponse.<Void>builder()
			.code("200")
			.message("토큰 재발급 성공")
			.build());
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(24 * 60 * 60);

		return cookie;
	}
}
