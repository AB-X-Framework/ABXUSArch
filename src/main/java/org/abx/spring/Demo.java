package org.abx.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.abx.spring",
        "org.abx.sec",
        "org.abx.jwt",
        "org.abx.services",
        "org.abx.heartbeat"})
public class Demo {

    public static void main(String[] args) {
        SpringApplication.run(Demo.class, args);
    }

}
