package com.matching.ezgg.domain.review.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.matching.ezgg.domain.riotApi.dto.MatchReviewDto;

/**
 * 리뷰 관련 유틸리티 클래스
 */
public final class ReviewUtil {
	/**
	 * List1에 없는 List2의 요소들을 반환하는 메서드
	 * @param list1
	 * @param list2
	 * @return List2에 있지만 List1에는 없는 요소들의 리스트
	 */
	public static List<String> getNewList(List<String> list1, List<String> list2) {
		Set<String> set1 = new HashSet<>(list1); // 빠른 조회를 위해 Set으로 변환
		return list2.stream()
			.filter(e -> !set1.contains(e))
			.collect(Collectors.toList());
	}
	/**
	 * 내 팀의 ID를 가져오는 메서드
	 * @param matchReviewDtoList
	 * @param riotUsername
	 * @return 내 팀의 ID (100, 200)
	 */
	public static int getMyTeamId(List<MatchReviewDto> matchReviewDtoList, String riotUsername) {
		for (MatchReviewDto matchReviewDto : matchReviewDtoList) {
			if (matchReviewDto.getRiotUsername().equals(riotUsername)) {
				return matchReviewDto.getTeamId();
			}
		}
		return -1;
	}
	/**
	 * 리뷰 가능 여부를 확인하는 메서드
	 * @param matchReviewDtoList
	 * @param partnerRiotUsername
	 * @param myTeamId
	 * @return 가능하면 true, 불가능하면 false
	 */
	public static boolean existsReviewableUser(List<MatchReviewDto> matchReviewDtoList, String partnerRiotUsername,
		int myTeamId) {
		for(MatchReviewDto matchReviewDto : matchReviewDtoList) {
			if(matchReviewDto.getRiotUsername().equals(partnerRiotUsername) && matchReviewDto.getTeamId() == myTeamId) {
				return true;
			}
		}
		return false;
	}
}
