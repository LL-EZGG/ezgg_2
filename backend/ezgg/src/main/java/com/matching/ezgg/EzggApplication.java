package com.matching.ezgg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EzggApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzggApplication.class, args);
	}

}
