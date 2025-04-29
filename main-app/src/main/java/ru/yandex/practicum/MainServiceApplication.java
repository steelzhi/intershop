package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.io.IOException;

@EnableR2dbcRepositories
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableCaching
public class MainServiceApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(MainServiceApplication.class, args);
	}
}
