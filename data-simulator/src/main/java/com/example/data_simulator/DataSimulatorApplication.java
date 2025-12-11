package com.example.data_simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataSimulatorApplication.class, args);
	}

}
