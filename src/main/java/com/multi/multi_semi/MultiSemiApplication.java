package com.multi.multi_semi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@SpringBootApplication
public class MultiSemiApplication {
    public static void main(String[] args) {
        System.out.println("Hello World");
        SpringApplication.run(MultiSemiApplication.class, args);
    }
}
