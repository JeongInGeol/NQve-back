package com.NQve.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NQveBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(NQveBackApplication.class, args);
		System.out.println("CI test");
	}

}
