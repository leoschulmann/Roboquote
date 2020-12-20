package com.leoschulmann.roboquote.WebFront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class WebFrontApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFrontApplication.class, args);
	}

}
