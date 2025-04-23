package com.matching.ezgg.api.domain.recentTwentyMatch.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.matching.ezgg.api.domain.recentTwentyMatch.ChampionStat;
import com.matching.ezgg.api.domain.recentTwentyMatch.repository.RecentTwentyMatchRepository;

@ActiveProfiles("test")
@DataJpaTest
class RecentTwentyMatchInfoTest {

	@Autowired
	private RecentTwentyMatchRepository recentTwentyMatchRepository;

	@Test
	void championStatsCanBeConverted() {
		Map<String, ChampionStat> championStats = new HashMap<>();

		ChampionStat ahri = ChampionStat.builder()
			.championName("ahri")
			.kills(30)
			.deaths(10)
			.assists(20)
			.build();

		ChampionStat yasuo = ChampionStat.builder()
			.championName("yasuo")
			.kills(15)
			.deaths(20)
			.assists(5)
			.build();

		ChampionStat lux = ChampionStat.builder()
			.championName("lux")
			.kills(22)
			.deaths(8)
			.assists(30)
			.build();

		championStats.put("Ahri", ahri);
		championStats.put("Yasuo", yasuo);
		championStats.put("Lux", lux);

		RecentTwentyMatch recentTwentyMatch = RecentTwentyMatch.builder()
			.sumKills(57)
			.sumDeaths(38)
			.sumAssists(55)
			.championStats(championStats)
			.build();

		recentTwentyMatchRepository.save(recentTwentyMatch);

		// 저장 후 다시 조회
		Optional<RecentTwentyMatch> retrieved = recentTwentyMatchRepository.findById(recentTwentyMatch.getId());

		retrieved.ifPresent(recentMatch -> {
			System.out.println("📌 JSON 저장된 championStats 출력:");
			recentMatch.getChampionStats().forEach((champ, stat) -> {
				System.out.printf("%s -> KDA: %d/%d/%d\n",
					champ, stat.getKills(), stat.getDeaths(), stat.getAssists());
			});
		});
	}
}
