package com.matching.ezgg.global.config;

import java.security.Principal;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.matching.ezgg.domain.chat.service.ChatRoomService;
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
	private final ChatRoomService chatRoomService;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		Principal principal = accessor.getUser();

		if (principal instanceof StompPrincipal) {
			String userId = principal.getName();
			sessionRegistry.register(userId);
			log.info("[INFO] {} 유저 웹소켓 연결됨", userId);
		}
	}

	@EventListener
	public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		Principal principal = accessor.getUser();
		String destination = accessor.getDestination();

		if (principal instanceof StompPrincipal && destination != null && destination.startsWith(
			"/user/queue/review")) {
			String userId = principal.getName();
			log.info("[INFO] {} 유저가 리뷰 큐를 구독함", userId);

			// 리뷰 확인 후 대기열에서 메시지를 소비
			List<String> pendingReviews = reviewNotificationService.consumePendingReviews(userId);
			if (!pendingReviews.isEmpty()) {
				pendingReviews.forEach(string -> {
					try {
						log.info("[INFO] {} 유저에게 리뷰 요청 전송: {}", userId, string);
						messagingTemplate.convertAndSendToUser(
							userId,
							"/queue/review",
							string
						);
					} catch (Exception e) {
						log.error("[ERROR] 리뷰 요청 전송 중 에러 발생: {}", e.getMessage());
					}
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

		if (principal instanceof StompPrincipal) {
			String userId = principal.getName();
			sessionRegistry.unregister(userId);
			log.info("[INFO] {} 유저 웹소켓 연결 해제됨", userId);

			// 사용자가 참여 중인 채팅방이 있는지 먼저 확인
			List<String> userChatRooms = chatRoomService.getChatRoomsForUser(userId);

			if (!userChatRooms.isEmpty()) {
				log.info("[INFO] {} 유저가 {} 개의 채팅방에서 연결 해제로 인해 자동 제거됨",
					userId, userChatRooms.size());

				// 각 채팅방에서 안전하게 제거 (DISCONNECT 이유로 처리)
				for (String chattingRoomId : userChatRooms) {
					try {
						// 해당 채팅방에 실제로 참여 중인지 다시 한번 확인
						if (chatRoomService.isUserInChatRoom(chattingRoomId, userId)) {
							// DISCONNECT 이유로 처리하여 수동 leave와 구분
							chatRoomService.handleUserLeave(chattingRoomId, userId, messagingTemplate, "DISCONNECT");
						} else {
							log.debug("[DEBUG] 유저 {}는 이미 채팅방 {}에서 제거됨", userId, chattingRoomId);
						}
					} catch (Exception e) {
						log.error("[ERROR] 채팅방 {} 에서 유저 {} 제거 중 오류: {}",
							chattingRoomId, userId, e.getMessage());
					}
				}
			} else {
				log.info("[INFO] {} 유저가 참여 중인 채팅방이 없어 추가 처리 없음", userId);
			}
		}
	}
}
