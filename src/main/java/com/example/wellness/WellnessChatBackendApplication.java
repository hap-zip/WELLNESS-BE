package com.example.wellness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WellnessChatBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WellnessChatBackendApplication.class, args);
    }

}