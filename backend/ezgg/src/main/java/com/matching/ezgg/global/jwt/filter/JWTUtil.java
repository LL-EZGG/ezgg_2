package com.matching.ezgg.global.jwt.filter;

import static io.jsonwebtoken.Jwts.SIG.*;
import static java.nio.charset.StandardCharsets.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.matching.ezgg.member.entity.Member;
import com.matching.ezgg.member.repository.MemberRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
public class JWTUtil {

	private final SecretKey key;
	private final MemberRepository memberRepository;

	public JWTUtil(@Value("${jwt.secret}") String key, MemberRepository memberRepository) {
		this.key = new SecretKeySpec(key.getBytes(UTF_8), HS256.key().build().getAlgorithm());
		this.memberRepository = memberRepository;
	}

	// 토큰 파싱 -> Claims 객체로 변환 (claims란 JWT의 payload에 해당하는 부분)
	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	// memberUsername 가져오기
	public String getMemberUsername(String token) {
		return parseClaims(token).get("memberUsername", String.class);
	}

	// memberId 가져오기
	public Long getMemberId(String token) {
		return parseClaims(token).get("memberId", Long.class);
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

	public String createJwt(String category, Long memberId, String memberUsername, String role, Long expiredMs) {
		return Jwts.builder()
			.claim("category", category)
			.claim("memberUsername", memberUsername)
			.claim("role", role)
			.claim("memberId", memberId)
			.issuedAt(new java.util.Date(System.currentTimeMillis()))
			.expiration(new java.util.Date(System.currentTimeMillis() + expiredMs))
			.signWith(key)
			.compact();
	}

}
