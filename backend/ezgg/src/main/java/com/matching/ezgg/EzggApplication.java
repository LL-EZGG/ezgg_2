package com.matching.ezgg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.matching.ezgg.es.repository")
public class EzggApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzggApplication.class, args);
	}

}
