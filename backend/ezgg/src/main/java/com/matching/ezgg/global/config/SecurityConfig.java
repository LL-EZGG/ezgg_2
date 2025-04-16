package com.matching.ezgg.global.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.matching.ezgg.global.jwt.filter.JWTFilter;
import com.matching.ezgg.global.jwt.filter.JWTUtil;
import com.matching.ezgg.global.jwt.filter.LoginFilter;
import com.matching.ezgg.global.jwt.repository.RefreshRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JWTUtil jwtUtil;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final RefreshRepository refreshRepository;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers("/favicon.ico")
			.requestMatchers("/error")
			.requestMatchers(toH2Console());
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// 기본 설정 비활성화
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);

		// URL 접근 권한 설정
		http.authorizeHttpRequests((auth) -> auth
			.requestMatchers("/auth/**", "/login").permitAll() // 모든 요청 허용 임시로
			.anyRequest().hasAnyAuthority("ROLE_USER"));

		// 세션 관리 설정
		http.sessionManagement((session) ->
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// JWT 필터 적용
		http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
			.addFilterBefore(new LoginFilter(jwtUtil, authenticationManager(authenticationConfiguration), refreshRepository),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
