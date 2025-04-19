package com.matching.ezgg.api.domain.memberInfo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.matching.ezgg.api.domain.memberInfo.entity.MemberInfo;

@Repository
public interface MemberInfoRepository extends JpaRepository<MemberInfo, Long> {
	@Query("SELECT m.puuid FROM MemberInfo m WHERE m.memberId = :memberId")
	Optional<String> findPuuidByMemberId(Long memberId);

	Optional<MemberInfo> findByPuuid(String puuid);
}
