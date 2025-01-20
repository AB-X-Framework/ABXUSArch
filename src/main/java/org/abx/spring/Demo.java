package org.abx.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.abx.jwt"})
public class Demo {

    public static void main(String[] args) {
        SpringApplication.run(Demo.class, args);
    }

}
