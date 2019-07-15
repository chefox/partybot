package com.portfolio.allinone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AllinoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllinoneApplication.class, args);
    }

}
