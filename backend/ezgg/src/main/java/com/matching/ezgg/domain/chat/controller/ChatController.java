package com.matching.ezgg.domain.chat.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.matching.ezgg.domain.chat.dto.ChatMessageDto;
import com.matching.ezgg.domain.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatRoomService chatRoomService;

	/**
	 *
	 * @param chatMessage
	 * @param principal
	 */
	@MessageMapping("/chat/send")
	public void sendChatMessage(ChatMessageDto chatMessage, Principal principal) {
		log.info("[INFO] 채팅 메시지 수신 - 방ID: {}, 발신자: {}, 메시지: {}",
			chatMessage.getChattingRoomId(), chatMessage.getSender(), chatMessage.getMessage());

		// 🔥 Principal 정보 확인
		log.info("[DEBUG] Principal 정보: {}", principal != null ? principal.getName() : "null");

		List<String> participants = chatRoomService.getParticipants(chatMessage.getChattingRoomId());

		if (participants.isEmpty()) {
			log.warn("[WARN] 채팅방에 참가자가 없습니다. 발신자를 추가합니다");
			chatRoomService.addParticipant(chatMessage.getChattingRoomId(), chatMessage.getSender());

			// 🔥 Principal 이름도 추가 (백업)
			if (principal != null && !principal.getName().equals(chatMessage.getSender())) {
				log.info("[INFO] Principal 이름도 참가자로 추가: {}", principal.getName());
				chatRoomService.addParticipant(chatMessage.getChattingRoomId(), principal.getName());
			}

			participants = chatRoomService.getParticipants(chatMessage.getChattingRoomId());
		}

		log.info("[INFO] 참가자 목록: {}", participants);

		// 🔥 Principal 이름으로도 전송 (백업)
		if (principal != null) {
			messagingTemplate.convertAndSendToUser(
				principal.getName(),
				"/queue/" + chatMessage.getChattingRoomId(),
				chatMessage
			);
			log.info("[INFO] Principal로 메시지 전송됨 - 수신자: {}", principal.getName());
		}

		// 브로드캐스트는 확실히 작동해야 함
		messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChattingRoomId(), chatMessage);
		log.info("[INFO] 브로드캐스트 메시지 전송됨");

		for (String participantId : participants) {
			messagingTemplate.convertAndSendToUser(
				participantId,
				"/queue/" + chatMessage.getChattingRoomId(),
				chatMessage
			);
			log.info("[INFO] 개별 메시지 전송됨 - 수신자: {}", participantId);
		}
	}
}



