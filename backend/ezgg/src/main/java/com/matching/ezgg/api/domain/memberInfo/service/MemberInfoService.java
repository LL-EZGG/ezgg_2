package com.matching.ezgg.api.domain.memberInfo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matching.ezgg.api.domain.memberInfo.entity.MemberInfo;
import com.matching.ezgg.api.domain.memberInfo.repository.MemberInfoRepository;
import com.matching.ezgg.api.dto.WinRateNTierDto;
import com.matching.ezgg.global.exception.MemberInfoNotFoundException;
import com.matching.ezgg.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberInfoService {
	private final MemberInfoRepository memberInfoRepository;
	private final MemberRepository memberRepository;

	// memberId로 puuid 조회
	public String getMemberPuuidByMemberId(Long memberId) {
		return memberInfoRepository.findPuuidByMemberId(memberId)
			.orElseThrow(MemberInfoNotFoundException::new);
	}


	// puuid로 MemberInfo 조회
	public MemberInfo getMemberInfoByPuuid(String puuid) {
		return memberInfoRepository.findByPuuid(puuid).orElseThrow(MemberInfoNotFoundException::new);
	}

	// MemberInfo에 tier, rank, wins, losses 업데이트
	@Transactional
	public void updateWinRateNTier(WinRateNTierDto winRateNTierDto) {
		//db에서 memberInfo 가져오기
		MemberInfo memberInfo = getMemberInfoByPuuid(winRateNTierDto.getPuuid());

		memberInfo.updateWinRateAndTier(
			winRateNTierDto.getTier(),
			winRateNTierDto.getRank(),
			winRateNTierDto.getWins(),
			winRateNTierDto.getLosses()
		); // 영속성 상태에서 Dirty Checking을 해 자동으로 db에 커밋됨
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
