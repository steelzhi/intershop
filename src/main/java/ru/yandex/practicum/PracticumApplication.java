package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootApplication
public class PracticumApplication {

	public static void main(String[] args) throws IOException {
/*

		File fi = new File("C:/Users/z/Downloads/beam.jpg");
		byte[] fileContent1 = Files.readAllBytes(fi.toPath());
		File file1 = new File("C:/Users/z/Downloads/beam..txt");
		FileOutputStream fos = new FileOutputStream(file1);
		fos.write(fileContent1);

		File fi2 = new File("C:/Users/z/Downloads/sheet.jpg");
		byte[] fileContent2 = Files.readAllBytes(fi2.toPath());
		File file2 = new File("C:/Users/z/Downloads/sheet.txt");
		FileOutputStream fos2 = new FileOutputStream(file2);
		fos2.write(fileContent2);

		File fi3 = new File("C:/Users/z/Downloads/armature.jpg");
		byte[] fileContent3 = Files.readAllBytes(fi3.toPath());
		File file3 = new File("C:/Users/z/Downloads/armature.txt");
		FileOutputStream fos3 = new FileOutputStream(file3);
		fos3.write(fileContent3);
*/


		SpringApplication.run(PracticumApplication.class, args);
	}

}
