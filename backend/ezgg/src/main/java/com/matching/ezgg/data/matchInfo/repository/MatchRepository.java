package com.matching.ezgg.data.matchInfo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.data.matchInfo.entity.MatchInfo;

public interface MatchRepository extends JpaRepository<MatchInfo, Long> {

	Optional<MatchInfo> findByMemberIdAndRiotMatchId(Long memberId, String matchId);
}
