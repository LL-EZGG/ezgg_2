package com.matching.ezgg.dataProcessor.matchInfo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.dataProcessor.matchInfo.entity.MatchInfo;

public interface MatchInfoRepository extends JpaRepository<MatchInfo, Long> {

	Optional<MatchInfo> findByMemberIdAndRiotMatchId(Long memberId, String matchId);
}
