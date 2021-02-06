package com.maxy.caller.admin;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author maxy
 */
@Log4j2
@EnableSwagger2
@SpringBootApplication(scanBasePackages = "com.maxy.caller",excludeName = {"com.maxy.caller.client","com.maxy.caller.remoting.client"})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
