package com.example.rag;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RagApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    @Bean
    ApplicationRunner onStart() {
        return args -> {
            System.out.println("RAG API is running.");
        };
    }
}
