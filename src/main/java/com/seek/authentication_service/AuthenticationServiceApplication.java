package com.seek.authentication_service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class AuthenticationServiceApplication {

    public static void main(String[] args) {
        log.info("Starting application init");
        SpringApplication.run(AuthenticationServiceApplication.class, args);
        log.info("Starting application end");
    }

}
