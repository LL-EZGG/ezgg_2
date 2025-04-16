package com.matching.ezgg.api.domain.recentTwentyMatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.api.domain.recentTwentyMatch.entity.RecentTwentyMatch;

public interface RecentTwentyMatchRepository extends JpaRepository<RecentTwentyMatch, Long> {
}
