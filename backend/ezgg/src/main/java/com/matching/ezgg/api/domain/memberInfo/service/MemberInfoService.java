package com.matching.ezgg.api.domain.memberInfo.service;

import org.springframework.stereotype.Service;

import com.matching.ezgg.api.domain.memberInfo.entity.MemberInfo;
import com.matching.ezgg.api.domain.memberInfo.repository.MemberInfoRepository;
import com.matching.ezgg.global.exception.MemberInfoException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberInfoService {
	private final MemberInfoRepository memberInfoRepository;

	//DB에 저장되어 있는 puuid 가져오기
	public String getMemberPuuid(Long memberId) {
		return memberInfoRepository.findPuuidByMemberId(memberId)
			.orElseThrow(MemberInfoException::new);
	}

	//member info 생성
	public void createNewMemberInfo(Long memberId, String riotUserName, String riotTag, String puuid) {
		memberInfoRepository.save(
			MemberInfo.builder()
				.memberId(memberId)
				.riotUsername(riotUserName)
				.riotTag(riotTag)
				.puuid(puuid)
				.build()
		);
		log.info("{}#{}의 새 memberInfo 생성", riotUserName, riotTag);
	}
}
