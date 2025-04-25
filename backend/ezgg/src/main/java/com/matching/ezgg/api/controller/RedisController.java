package com.matching.ezgg.api.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matching.ezgg.api.dto.RedisDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/redis")
@RestController
public class RedisController {

	private final RedisTemplate<String, Object> redisTemplate;

	@PostMapping
	public ResponseEntity<String> set(@RequestBody RedisDto dto) {
		redisTemplate.opsForValue().set(dto.getKey(), dto.getValue());
		return ResponseEntity.ok("success");
	}

	@GetMapping
	public ResponseEntity<Object> get(@RequestParam("key") String key) {
		Object value = redisTemplate.opsForValue().get(key);
		System.out.println(value);
		return ResponseEntity.ok(value);
	}
}
