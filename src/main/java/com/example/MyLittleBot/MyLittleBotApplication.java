package com.example.MyLittleBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyLittleBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyLittleBotApplication.class, args);
	}
}
