package com.matching.ezgg.api.domain.memberinfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matching.ezgg.api.domain.memberinfo.entity.MemberInfo;

public interface MemberInfoRepository extends JpaRepository<MemberInfo, Long> {
}
