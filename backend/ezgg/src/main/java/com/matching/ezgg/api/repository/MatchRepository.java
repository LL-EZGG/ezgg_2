package com.matching.ezgg.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.api.entity.MatchInfo;

public interface MatchRepository extends JpaRepository<MatchInfo, Long> {

	Optional<MatchInfo> findByMemberIdAndRiotMatchId(Long memberId, String matchId);
}
