package com.matching.ezgg.global.jwt.repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";
    private static final String BLACKLIST_TOKEN_PREFIX = "blacklist:";

    /**
     * Refresh Token을 Redis에 저장합니다.
     * key는 "refreshToken:{memberId}" 형태로 저장됩니다.
     */
    public void save(String memberId, String refreshToken, long expirationTimeMs) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisTemplate.opsForValue().set(key, refreshToken);
        redisTemplate.expire(key, expirationTimeMs, TimeUnit.MILLISECONDS);
    }

    /**
     * MemberId로 Redis에서 Refresh Token을 조회합니다.
     */
    public String findByMemberId(String memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * MemberId에 해당하는 Refresh Token을 삭제합니다.
     */
    public void deleteByMemberId(String memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisTemplate.delete(key);
    }

    /**
     * Refresh Token이 Redis에 존재하는지 확인합니다.
     */
    public boolean existsByRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().getOperations().keys(REFRESH_TOKEN_PREFIX + "*")
                .stream()
                .anyMatch(key -> refreshToken.equals(redisTemplate.opsForValue().get(key)));
    }
    
    /**
     * Access Token을 블랙리스트에 추가합니다.
     * 토큰이 만료될 때까지만 블랙리스트에 유지합니다.
     */
    public void addToBlacklist(String accessToken, long remainingTimeMs) {
        String key = BLACKLIST_TOKEN_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, "logout");
        redisTemplate.expire(key, remainingTimeMs, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Access Token이 블랙리스트에 있는지 확인합니다.
     */
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_TOKEN_PREFIX + accessToken;
        return redisTemplate.hasKey(key);
    }
} 