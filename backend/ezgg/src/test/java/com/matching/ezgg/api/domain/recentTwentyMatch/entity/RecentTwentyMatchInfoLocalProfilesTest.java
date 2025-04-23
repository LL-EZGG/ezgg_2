package com.matching.ezgg.api.domain.recentTwentyMatch.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.matching.ezgg.api.domain.recentTwentyMatch.repository.RecentTwentyMatchRepository;

@ActiveProfiles("local")
@DataJpaTest
class RecentTwentyMatchInfoLocalProfilesTest {

	@Autowired
	private RecentTwentyMatchRepository recentTwentyMatchRepository;

	@Test
	void localDBChampionStatsCanBeConverted() {
		RecentTwentyMatch recentTwentyMatch = recentTwentyMatchRepository.findById(1L).get();

		recentTwentyMatch.getChampionStats().forEach((champ, stat) -> {
			System.out.printf("%s -> KDA: %d/%d/%d\n",
				champ, stat.getKills(), stat.getDeaths(), stat.getAssists());
		});
	}
}
