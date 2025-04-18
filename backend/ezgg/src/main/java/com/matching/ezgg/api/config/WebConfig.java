package com.matching.ezgg.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

	@Bean(name = "asia")
	public WebClient asiaWebClient() {
		return WebClient.builder()
			.baseUrl("https://asia.api.riotgames.com")
			.build();
	}

	@Bean(name = "kr")
	public WebClient krWebClient() {
		return WebClient.builder()
			.baseUrl("https://kr.api.riotgames.com")
			.build();
	}
}
