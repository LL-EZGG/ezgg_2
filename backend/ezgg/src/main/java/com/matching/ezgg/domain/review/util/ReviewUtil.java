package com.matching.ezgg.domain.review.util;

import java.util.HashSet;
import java.util.List;

import com.matching.ezgg.domain.riotApi.dto.MatchReviewDto;

/**
 * 리뷰 관련 유틸리티 클래스
 */
public final class ReviewUtil {
	/**
	 * 리뷰를 남길 때, 두 유저의 매치 아이디 리스트가 같은지 확인하는 메서드
	 * @param list1
	 * @param list2
	 * @return 같으면 true, 다르면 false
	 */
	public static boolean isSameMatchIdList(List<String> list1, List<String> list2) {
		if (list1 == null || list2 == null) return false;
		if (list1.size() != list2.size()) return false;

		return new HashSet<>(list1).equals(new HashSet<>(list2));
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
