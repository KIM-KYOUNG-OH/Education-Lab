package com.project.educationLab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class EducationLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(EducationLabApplication.class, args);
	}

}
