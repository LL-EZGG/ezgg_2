package com.matching.ezgg.api.domain.memberinfo;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.matching.ezgg.api.domain.memberinfo.entity.MemberInfo;
import com.matching.ezgg.api.domain.memberinfo.repository.MemberInfoRepository;

@ActiveProfiles("test")
@DataJpaTest
class MemberInfoTest {

	@Autowired
	private MemberInfoRepository memberInfoRepository;

	@Test
	void matchIdsCanBeConverted() {
		// given
		MemberInfo member = MemberInfo.builder()
			.puuid("test-puuid-1234")
			.matchIds(List.of("blah", "blah"))
			.wins(1L)
			.losses(3L)
			.build();

		// when
		MemberInfo saved = memberInfoRepository.save(member);
		MemberInfo found = memberInfoRepository.findById(saved.getId()).orElseThrow();

		// then
		assertThat(found.getMatchIds()).isNotNull();
		assertThat(found.getMatchIds().size()).isEqualTo(2);
		assertThat(found.getMatchIds()).containsExactly("배열1", "배열2");

		System.out.println("✔ 저장된 matchIds: " + found.getMatchIds());
	}
}
