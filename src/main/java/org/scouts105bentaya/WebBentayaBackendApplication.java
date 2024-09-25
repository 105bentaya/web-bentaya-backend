package org.scouts105bentaya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WebBentayaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBentayaBackendApplication.class, args);
	}

}