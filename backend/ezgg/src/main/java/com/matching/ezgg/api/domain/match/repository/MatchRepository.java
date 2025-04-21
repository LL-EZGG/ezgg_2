package com.matching.ezgg.api.domain.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matching.ezgg.api.domain.match.entity.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

}
