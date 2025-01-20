package org.abx.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.abx.spring",
        "org.abx.jwt",
        "org.abx.controller"})
public class Demo {

    public static void main(String[] args) {
        SpringApplication.run(Demo.class, args);
    }

}
