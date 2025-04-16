package com.matching.ezgg.menber.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.menber.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByMemberId(String memberId);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByRiotUsername(String riotUsername);

	Optional<Member> findByRiotTag(String riotTag);
}

