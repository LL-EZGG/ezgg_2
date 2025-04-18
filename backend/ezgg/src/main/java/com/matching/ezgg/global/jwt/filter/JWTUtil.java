package com.matching.ezgg.global.jwt.filter;

import static io.jsonwebtoken.Jwts.SIG.*;
import static java.nio.charset.StandardCharsets.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil {

	private final SecretKey key;

	public JWTUtil(@Value("${jwt.secret}") String key) {
		this.key = new SecretKeySpec(key.getBytes(UTF_8), HS256.key().build().getAlgorithm());
	}

	// 토큰 파싱 -> Claims 객체로 변환 (claims란 JWT의 payload에 해당하는 부분)
	private Claims parseClaims(String toekn) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(toekn).getPayload();
	}

	// memberUsername 가져오기
	public String getMemberUsername(String toeken) {
		return parseClaims(toeken).get("memberUsername", String.class);
	}

	// 권한 정보 가져오기
	public String getRole(String token) {
		return parseClaims(token).get("role", String.class);
	}

	// 카테고리 정보 가져오기 (access, refresh)
	public String getCategory(String token) {
		return parseClaims(token).get("category", String.class);
	}

	// 토큰 만료 여부 확인
	public Boolean isExpired(String token) {
		return parseClaims(token).getExpiration().before(new java.util.Date());
	}

	public String createJwt(String category, String memberUsername, String role, Long expiredMs) {
		return Jwts.builder()
			.claim("category", category)
			.claim("memberUsername", memberUsername)
			.claim("role", role)
			.issuedAt(new java.util.Date(System.currentTimeMillis()))
			.expiration(new java.util.Date(System.currentTimeMillis() + expiredMs))
			.signWith(key)
			.compact();
	}

}
