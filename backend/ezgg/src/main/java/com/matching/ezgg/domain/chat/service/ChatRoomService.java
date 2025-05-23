package com.matching.ezgg.domain.chat.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatRoomService {

	// 채팅방 ID → 참여자 리스트 (유저 ID or username)
	private final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> chatRoomParticipants = new ConcurrentHashMap<>();

	/**
	 * 채팅방에 참여자 추가
	 * @param chattingRoomId 채팅방 ID
	 * @param memberId 참여자 ID (유저 ID 또는 username)
	 */
	public void addParticipant(String chattingRoomId, String memberId) {
		if (chattingRoomId == null || memberId == null) {
			log.warn("[WARN] 채팅방 ID 또는 멤버 ID가 null입니다. chattingRoomId: {}, memberId: {}", chattingRoomId, memberId);
			return;
		}

		chatRoomParticipants
			.computeIfAbsent(chattingRoomId, key -> new CopyOnWriteArrayList<>())
			.addIfAbsent(memberId);

		log.info("[INFO] 채팅방 참여자 추가됨 - 방ID: {}, 멤버ID: {}, 현재 참여자 수: {}",
			chattingRoomId, memberId, getParticipantCount(chattingRoomId));
	}

	/**
	 * 채팅방에서 참여자 제거
	 * @param chattingRoomId 채팅방 ID
	 * @param memberId 참여자 ID
	 */
	public void removeParticipant(String chattingRoomId, String memberId) {
		if (chattingRoomId == null || memberId == null) {
			log.warn("[WARN] 채팅방 ID 또는 멤버 ID가 null입니다. chattingRoomId: {}, memberId: {}", chattingRoomId, memberId);
			return;
		}

		List<String> participants = chatRoomParticipants.get(chattingRoomId);
		if (participants != null) {
			boolean removed = participants.remove(memberId);
			if (removed) {
				log.info("[INFO] 채팅방 참여자 제거됨 - 방ID: {}, 멤버ID: {}, 현재 참여자 수: {}",
					chattingRoomId, memberId, participants.size());

				// 참여자가 모두 나가면 채팅방 정보 삭제
				if (participants.isEmpty()) {
					chatRoomParticipants.remove(chattingRoomId);
					log.info("[INFO] 채팅방 삭제됨 - 방ID: {} (참여자 0명)", chattingRoomId);
				}
			}
		}
	}

	/**
	 * 채팅방 참여자 목록 반환
	 * @param chattingRoomId 채팅방 ID
	 * @return 참여자 ID 리스트
	 */
	public List<String> getParticipants(String chattingRoomId) {
		if (chattingRoomId == null) {
			log.warn("[INFO] 채팅방 ID가 null입니다.");
			return new CopyOnWriteArrayList<>();
		}

		List<String> participants = chatRoomParticipants.getOrDefault(chattingRoomId, new CopyOnWriteArrayList<>());
		log.debug("[INFO] 채팅방 참여자 조회 - 방ID: {}, 참여자 수: {}, 참여자 목록: {}",
			chattingRoomId, participants.size(), participants);

		return participants;
	}

	/**
	 * 채팅방 참여자 수 반환
	 * @param chattingRoomId 채팅방 ID
	 * @return 참여자 수
	 */
	public int getParticipantCount(String chattingRoomId) {
		return getParticipants(chattingRoomId).size();
	}
}
