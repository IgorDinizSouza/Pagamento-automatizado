package com.cobranca.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner() {
		return args -> {
			System.out.println("🔍 VARIÁVEIS DE AMBIENTE:");
			System.getenv().forEach((k, v) -> {
				if (k.toLowerCase().contains("spring") || k.toLowerCase().contains("db") || k.toLowerCase().contains("pg") || k.toLowerCase().contains("railway")) {
					System.out.println(k + "=" + v);
				}
			});
		};
	}


}

