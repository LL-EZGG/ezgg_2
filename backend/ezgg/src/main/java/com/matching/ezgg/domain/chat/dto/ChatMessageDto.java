package com.matching.ezgg.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
	private String type;
	private String chattingRoomId;
	private String message;
	private String sender;
	private String timestamp;
}
