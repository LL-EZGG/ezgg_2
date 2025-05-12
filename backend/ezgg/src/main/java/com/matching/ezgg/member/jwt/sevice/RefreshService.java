package com.matching.ezgg.member.jwt.sevice;

import org.springframework.stereotype.Service;

import com.matching.ezgg.global.exception.InvalidTokenException;
import com.matching.ezgg.member.jwt.filter.JWTUtil;
import com.matching.ezgg.member.jwt.repository.RedisRefreshTokenRepository;

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

	private final static long ACCESS_TOKEN_EXPIRY = 60 * 60 * 1000L; // 1시간
	private final static long REFRESH_TOKEN_EXPIRY = 24 * 60 * 60 * 1000L; // 24시간

	/**
	 * Refresh 토큰을 검증하고 추출하는 메서드
	 * @param request
	 * @return 검증된 Refresh 토큰
	 */
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


		String memberUsername = jwtUtil.getMemberUsername(refreshToken); // 토큰에서 memberUsername 추출
		String savedRefreshToken = redisRefreshTokenRepository.findByMemberId(memberUsername); // Redis에서 memberUsername으로 저장된 Refresh 토큰 조회

		if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
			log.error(">>>>> 유효하지 않은 Refresh 토큰입니다.");
			throw new InvalidTokenException();
		}

		return refreshToken;
	}

	/**
	 * Refresh 토큰을 사용하여 새로운 Access Token과 Refresh Token을 생성하는 메서드
	 * @param refreshToken
	 * @return 새로운 Access Token과 Refresh Token을 포함하는 TokenPair 객체
	 */
	public TokenPair generateAndDeleteAndSaveNewTokens(String refreshToken) {
		Long memberId = jwtUtil.getMemberId(refreshToken); // 토큰에서 pk값 추출
		String memberUsername = jwtUtil.getMemberUsername(refreshToken); // 토큰에서 memberUsername 추출
		String role = jwtUtil.getRole(refreshToken); // 토큰에서 권한 추출

		String newAccessToken = jwtUtil.createJwt("access", memberId, memberUsername, role, ACCESS_TOKEN_EXPIRY);
		String newRefreshToken = jwtUtil.createJwt("refresh", memberId, memberUsername, role, REFRESH_TOKEN_EXPIRY);

		// Redis에서 기존 Refresh Token 삭제 후 새로운 Refresh Token 저장
		redisRefreshTokenRepository.deleteByMemberId(memberUsername);
		redisRefreshTokenRepository.save(memberUsername, newRefreshToken, REFRESH_TOKEN_EXPIRY);

		return new TokenPair(newAccessToken, newRefreshToken);
	}

	/**
	 * HTTP 요청에서 쿠키를 추출하는 메서드
	 * @param request
	 * @param name
	 * @return 쿠키 값
	 */
	private String extractCookie(HttpServletRequest request, String name) {
		if (request.getCookies() == null) return null;
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * Access Token과 Refresh Token을 포함하는 레코드 클래스
	 */
	public record TokenPair(String accessToken, String refreshToken) {}
}
