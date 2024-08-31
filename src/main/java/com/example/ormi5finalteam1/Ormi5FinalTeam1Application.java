package com.example.ormi5finalteam1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class Ormi5FinalTeam1Application {

	public static void main(String[] args) {
		SpringApplication.run(Ormi5FinalTeam1Application.class, args);
	}

}
