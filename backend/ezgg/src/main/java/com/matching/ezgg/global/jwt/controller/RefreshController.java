package com.matching.ezgg.global.jwt.controller;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.global.exception.InvalidTokenException;
import com.matching.ezgg.global.jwt.entity.Refresh;
import com.matching.ezgg.global.jwt.filter.JWTUtil;
import com.matching.ezgg.global.jwt.repository.RefreshRepository;
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
	private final RefreshRepository refreshRepository;

	@PostMapping("/refresh")
	public ResponseEntity<SuccessResponse<Void>> refresh(HttpServletRequest request, HttpServletResponse response) {
		String refresh = null;

		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("Refresh")) {
				refresh = cookie.getValue();
			}
		}

		if (refresh == null) {
			log.info(">>>>> refresh-token이 없습니다.");
			throw new InvalidTokenException();
		}

		if (!jwtUtil.getCategory(refresh).equals("refresh")) {
			log.error(">>>>> refresh-token이 아닙니다.");
			throw new InvalidTokenException();
		}

		if (!refreshRepository.existsByRefresh(refresh)) {
			log.error(">>>>> 존재하지 않는 refresh-token입니다.");
			throw new InvalidTokenException();
		}

		String memberUsername = jwtUtil.getMemberUsername(refresh);
		Long memberId = jwtUtil.getMemberId(refresh);
		String role = jwtUtil.getRole(refresh);

		String newAccessToken = jwtUtil.createJwt("access", memberId, memberUsername, role, 60 * 60 * 1000L);
		String newRefreshToken = jwtUtil.createJwt("refresh", memberId, memberUsername, role, 24 * 60 * 60 * 1000L);

		refreshRepository.deleteByRefresh(refresh);
		addRefreshEntity(memberUsername, newRefreshToken, 24 * 60 * 60 * 1000L);

		response.setHeader("Authorization", newAccessToken);
		response.addCookie(createCookie("Refresh", newRefreshToken));

		return ResponseEntity.ok(SuccessResponse.<Void>builder()
			.code("200")
			.message("토근 재발급 성공")
			.build());
	}

	private void addRefreshEntity(String memberUsername, String refreshToken, Long expiredMs) {
		Refresh refresh = Refresh.builder()
			.memberId(memberUsername)
			.refresh(refreshToken)
			.expiration(String.valueOf(new Date(System.currentTimeMillis() + expiredMs)))
			.build();

		refreshRepository.save(refresh);
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(24 * 60 * 60);

		return cookie;
	}
}
