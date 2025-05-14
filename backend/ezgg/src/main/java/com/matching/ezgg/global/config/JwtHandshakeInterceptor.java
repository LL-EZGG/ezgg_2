package com.matching.ezgg.global.config;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.matching.ezgg.member.jwt.filter.JWTUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

	private final JWTUtil jWTUtil;

	@Override
	public boolean beforeHandshake(
		ServerHttpRequest request,
		ServerHttpResponse response,
		WebSocketHandler wsHandler,
		Map<String, Object> attributes
	) {
		URI uri = request.getURI();
		String query = uri.getQuery(); // token=xxxx

		if (query != null && query.contains("token=")) {
			String token = query.substring(query.indexOf("token=") + 13);
			try {
				if(jWTUtil.isExpired(token)) {
					response.setStatusCode(HttpStatus.UNAUTHORIZED);
					return false;
				}
				attributes.put("token", token);
				return true;
			} catch (Exception e) {
				response.setStatusCode(HttpStatus.BAD_REQUEST);
				return false;
			}
		}
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		return false;
	}

	@Override
	public void afterHandshake(
		ServerHttpRequest request,
		ServerHttpResponse response,
		WebSocketHandler wsHandler,
		Exception exception
	) {
		// Nothing to do
	}
}
