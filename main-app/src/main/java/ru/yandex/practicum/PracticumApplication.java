package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.io.IOException;

@EnableR2dbcRepositories
@SpringBootApplication
@EnableCaching
public class PracticumApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(PracticumApplication.class, args);
	}

}
