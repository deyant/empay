package com.example.empay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;

@SpringBootApplication(exclude = { QuartzAutoConfiguration.class })
public class EmpayApplication {
    /**
     * Run the application from a command line.
     *
     * @param args CLI arguments.
     */
    public static void main(final String[] args) {
        SpringApplication.run(EmpayApplication.class, args);

    }
}
