package dev.cantrella.ms_code_elevate.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MsCodeElevateApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCodeElevateApplication.class, args);
	}

}
