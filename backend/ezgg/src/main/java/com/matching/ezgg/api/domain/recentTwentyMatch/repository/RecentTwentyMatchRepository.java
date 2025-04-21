package com.matching.ezgg.api.domain.recentTwentyMatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matching.ezgg.api.domain.recentTwentyMatch.entity.RecentTwentyMatch;

@Repository
public interface RecentTwentyMatchRepository extends JpaRepository<RecentTwentyMatch, Long> {
	boolean existsByMemberId(Long matchId);

	Optional<RecentTwentyMatch> findByMemberId(Long matchId);
}
