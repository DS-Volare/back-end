package com.example.volare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication( exclude = SecurityAutoConfiguration.class)
@EnableJpaAuditing
@EnableCaching
public class VolareApplication {
	public static void main(String[] args) {
		SpringApplication.run(VolareApplication.class, args);
	}

}
