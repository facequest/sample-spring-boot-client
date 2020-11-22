package com.facequest.faceverification.demo;

import com.facequest.faceverification.demo.controller.HomeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

@EnableAutoConfiguration
@SpringBootApplication
public class DemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        logger.info("Server is running on localhost:8080");

        logger.info("Kick-starting a verification request from the code, you could as well do it from the browser");

        WebClient.create().get().uri("localhost:8080/").exchange().subscribe(response -> response.bodyToMono(String.class));
    }

}
