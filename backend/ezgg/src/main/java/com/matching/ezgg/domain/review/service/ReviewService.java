package com.matching.ezgg.domain.review.service;

import static com.matching.ezgg.domain.review.util.ReviewUtil.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.matching.ezgg.domain.matching.infra.redis.service.RedisService;
import com.matching.ezgg.domain.memberInfo.entity.MemberInfo;
import com.matching.ezgg.domain.memberInfo.service.MemberInfoService;
import com.matching.ezgg.domain.review.dto.CreateReviewDto;
import com.matching.ezgg.domain.review.entity.Review;
import com.matching.ezgg.domain.review.repository.ReviewRepository;
import com.matching.ezgg.domain.riotApi.dto.MatchReviewDto;
import com.matching.ezgg.domain.riotApi.service.ApiService;
import com.matching.ezgg.domain.riotApi.util.MatchMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final MemberInfoService memberInfoService;
	private final ReviewNotificationService reviewNotificationService;
	private final RedisService redisService;
	private final ApiService apiService;
	private final MatchMapper matchMapper;

	public void findDuoGame(Long memberId1, Long memberId2, Map<String, String> updateMatchedUser) {
		MemberInfo memberInfoByMember1 = memberInfoService.getMemberInfoByMemberId(memberId1);
		MemberInfo memberInfoByMember2 = memberInfoService.getMemberInfoByMemberId(memberId2);

		List<String> findMember1MatchIds = apiService.getMemberMatchIds(memberInfoByMember1.getPuuid());
		List<String> findMember2MatchIds = apiService.getMemberMatchIds(memberInfoByMember2.getPuuid());

		List<String> commonElements = getCommonElements(findMember1MatchIds, findMember2MatchIds);

		for(String matchId : commonElements) {
			String match = apiService.getMatch(matchId);
			List<MatchReviewDto> matchReviewDtoList = matchMapper.toMatchReviewDto(match);
			int myTeamId = getMyTeamId(matchReviewDtoList, memberInfoByMember1.getRiotUsername());
			if (existsReviewableUser(matchReviewDtoList, memberInfoByMember2.getRiotUsername(), myTeamId)) {
				// 리뷰 저장 (score는 0으로 초기화)
				reviewRepository.save(
					Review.builder()
						.memberId(memberId1)
						.partnerMemberId(memberId2)
						.partnerRiotUsername(matchId)
						.matchId(findMember1MatchIds.getFirst())
						.reviewScore(0)
						.build()
				);
				reviewRepository.save(
					Review.builder()
						.memberId(memberId2)
						.partnerMemberId(memberId1)
						.partnerRiotUsername(memberInfoByMember1.getRiotUsername())
						.matchId(matchId)
						.reviewScore(0)
						.build()
				);
				// redis에서 실제 듀오게임 한 유저 삭제
				redisService.deleteMatchedUser(updateMatchedUser);
				// 리뷰 요청 전송
				reviewNotificationService.sendOrQueueReview(memberId1, memberInfoByMember1.getRiotUsername(), memberId2,
					memberInfoByMember2.getRiotUsername());
				break;
			}
		}
	}
}
