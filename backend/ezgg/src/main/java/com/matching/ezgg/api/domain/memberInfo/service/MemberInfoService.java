package com.matching.ezgg.api.domain.memberInfo.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

	// puuid로 memberId 조회
	public Long getMemberIdByPuuid(String puuid) {
		return memberInfoRepository.findMemberIdByPuuid(puuid)
			.orElseThrow(MemberInfoNotFoundException::new);
	}

	// puuid로 MemberInfo 조회
	public MemberInfo getMemberInfoByPuuid(String puuid) {
		return memberInfoRepository.findByPuuid(puuid).orElseThrow(MemberInfoNotFoundException::new);
	}

	// MemberInfo에 tier, rank, wins, losses 업데이트
	@Transactional
	public void updateWinRateNTier(WinRateNTierDto winRateNTierDto) {
		log.info("티어, 승률 업데이트 시작");

		// db에서 memberInfo 가져오기
		MemberInfo memberInfo = getMemberInfoByPuuid(winRateNTierDto.getPuuid());

		memberInfo.updateWinRateAndTier(
			winRateNTierDto.getTier(),
			winRateNTierDto.getRank(),
			winRateNTierDto.getWins(),
			winRateNTierDto.getLosses()
		); // 영속성 상태에서 Dirty Checking을 해 자동으로 db에 커밋됨

		log.info("티어, 승률 업데이트 종료: {}{}", winRateNTierDto.getTier(), winRateNTierDto.getRank());
	}

	//member info 생성
	public MemberInfo createNewMemberInfo(Long memberId, String riotUserName, String riotTag,
		String puuid) {//TODO 트랜잭션을 memberService가 아니라 여기서??

		log.info("{}#{}의 새 memberInfo 생성 시작", riotUserName, riotTag);

		MemberInfo memberInfo = MemberInfo.builder()
			.memberId(memberId)
			.riotUsername(riotUserName)
			.riotTag(riotTag)
			.puuid(puuid)
			.build();

		memberInfoRepository.save(memberInfo);
		log.info("{}#{}의 새 memberInfo 생성 종료", riotUserName, riotTag);

		return memberInfo;
	}

	//기존 matchIds와 새로운 matchIds 비교 후 새롭게 추가된 matchId 리스트를 리턴
	public List<String> extractNewMatchIds(String puuid, List<String> fetchedMatchIds) {
		// 기존 matchIds가 null이면 빈 리스트로 대체 TODO memberInfo에 항상 빈 리스트를 채워놓도록 refactor
		List<String> existingMatchIds = Optional.ofNullable(getMemberInfoByPuuid(puuid).getMatchIds())
			.orElse(Collections.emptyList());

		Set<String> existingMathIdSet = new HashSet<>(existingMatchIds);

		return fetchedMatchIds.stream()
			.filter(matchId -> !existingMathIdSet.contains(matchId))
			.collect(Collectors.toList());
	}

	// MemberInfo에 matchIds 업데이트
	@Transactional
	public void updateMatchIds(String puuid, List<String> fetchedMatchIds) {
		log.info("matchIds 업데이트 시작");
		MemberInfo memberInfo = getMemberInfoByPuuid(puuid);
		memberInfo.updateMatchIds(
			fetchedMatchIds
		); // 영속성 상태에서 Dirty Checking을 해 자동으로 db에 커밋됨
		log.info("matchIds 업데이트 종료");
	}
}
