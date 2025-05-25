package com.matching.ezgg.global.config;

import java.security.Principal;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.matching.ezgg.domain.review.service.ReviewNotificationService;
import com.matching.ezgg.global.common.StompPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

	private final SessionRegistry sessionRegistry;
	private final ReviewNotificationService reviewNotificationService;
	private final SimpMessagingTemplate messagingTemplate;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		Principal principal = accessor.getUser();

		if(principal instanceof StompPrincipal) {
			String userId = principal.getName();
			sessionRegistry.register(userId);

			// 리뷰 확인 후 대기열에서 메시지를 소비
			List<String> pendingReviews = reviewNotificationService.consumePendingReviews(userId);
			if(!pendingReviews.isEmpty()) {
				pendingReviews.forEach(partnerRiotUsername -> {
					log.info("[INFO] {} 유저에게 {} 유저의 리뷰 요청", userId, partnerRiotUsername);
					messagingTemplate.convertAndSendToUser(
						userId,
						"/queue/review",
						partnerRiotUsername
					);
				});
			} else {
				log.info("[INFO] {} 유저 작성할 리뷰 없음", userId);
			}
		}
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		Principal principal = accessor.getUser();

		if(principal instanceof StompPrincipal) {
			String userId = principal.getName();
			sessionRegistry.unregister(userId);
		}
	}
}
