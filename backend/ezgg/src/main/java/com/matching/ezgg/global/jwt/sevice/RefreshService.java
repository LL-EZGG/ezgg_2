package com.matching.ezgg.global.jwt.sevice;

import org.springframework.stereotype.Service;

import com.matching.ezgg.global.exception.InvalidTokenException;
import com.matching.ezgg.global.jwt.filter.JWTUtil;
import com.matching.ezgg.global.jwt.repository.RedisRefreshTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshService {

	private final JWTUtil jwtUtil;
	private final RedisRefreshTokenRepository redisRefreshTokenRepository;

	public String validateAndExtractRefreshToken(HttpServletRequest request) {
		String refreshToken = extractCookie(request, "Refresh");

		if (refreshToken == null) {
			log.info(">>>>> Refresh 토큰이 없습니다.");
			throw new InvalidTokenException();
		}

		if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
			log.error(">>>>> refresh-token이 아닙니다.");
			throw new InvalidTokenException();
		}

		String memberUsername = jwtUtil.getMemberUsername(refreshToken);
		String savedRefreshToken = redisRefreshTokenRepository.findByMemberId(memberUsername);

		if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
			log.error(">>>>> 유효하지 않은 Refresh 토큰입니다.");
			throw new InvalidTokenException();
		}

		return refreshToken;
	}

	public TokenPair generateNewTokens(String refreshToken) {
		Long memberId = jwtUtil.getMemberId(refreshToken);
		String memberUsername = jwtUtil.getMemberUsername(refreshToken);
		String role = jwtUtil.getRole(refreshToken);

		long accessTokenExpiry = 60 * 60 * 1000L;
		long refreshTokenExpiry = 24 * 60 * 60 * 1000L;

		String newAccessToken = jwtUtil.createJwt("access", memberId, memberUsername, role, accessTokenExpiry);
		String newRefreshToken = jwtUtil.createJwt("refresh", memberId, memberUsername, role, refreshTokenExpiry);

		redisRefreshTokenRepository.deleteByMemberId(memberUsername);
		redisRefreshTokenRepository.save(memberUsername, newRefreshToken, refreshTokenExpiry);

		return new TokenPair(newAccessToken, newRefreshToken);
	}

	private String extractCookie(HttpServletRequest request, String name) {
		if (request.getCookies() == null) return null;
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public record TokenPair(String accessToken, String refreshToken) {}
}
