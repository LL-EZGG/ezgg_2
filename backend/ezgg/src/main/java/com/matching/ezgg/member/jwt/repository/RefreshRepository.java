package com.matching.ezgg.member.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.member.jwt.entity.Refresh;

import jakarta.transaction.Transactional;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
	@Transactional
	void deleteByRefresh(String refresh);

	boolean existsByRefresh(String refresh);
}
