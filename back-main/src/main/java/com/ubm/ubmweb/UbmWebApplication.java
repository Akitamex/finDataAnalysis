package com.ubm.ubmweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling

// @EnableJpaRepositories("com.ubm.ubmweb.repository")
public class UbmWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(UbmWebApplication.class, args);
	}
}
