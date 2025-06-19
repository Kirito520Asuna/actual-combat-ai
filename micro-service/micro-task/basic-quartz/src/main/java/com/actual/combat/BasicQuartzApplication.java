package com.actual.combat;

import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = ShiroWebAutoConfiguration.class)
public class BasicQuartzApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicQuartzApplication.class, args);
    }

}
