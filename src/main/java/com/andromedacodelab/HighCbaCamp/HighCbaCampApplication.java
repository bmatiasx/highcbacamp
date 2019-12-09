package com.andromedacodelab.HighCbaCamp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class HighCbaCampApplication {

	public static void main(String[] args) {
		SpringApplication.run(HighCbaCampApplication.class, args);
	}

}
