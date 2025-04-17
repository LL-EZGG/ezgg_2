package com.matching.ezgg.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByMemberId(String memberId);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByRiotUsername(String riotUsername);

	Optional<Member> findByRiotTag(String riotTag);

	boolean existsByMemberId(String memberId);

	boolean existsByEmail(String email);

	boolean existsByRiotUsername(String riotUsername);

	boolean existsByRiotTag(String riotTag);
}

