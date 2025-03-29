package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@EnableR2dbcRepositories
@SpringBootApplication
public class PracticumApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(PracticumApplication.class, args);
	}

}
