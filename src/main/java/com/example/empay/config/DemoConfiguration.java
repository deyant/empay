package com.example.empay.config;

import com.example.empay.repository.security.UserLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Print instructions how to use this application when started in DEV profile.
 */
//@Profile("dev")
@Component
@Slf4j
public class DemoConfiguration {

    /**
     * UserLogin repository.
     */
    @Autowired
    private UserLoginRepository userLoginRepository;

    /**
     * Called when the application is started and ready to serve.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStarted() {
        log.info("******************************************************");
        log.info("Database console:\thttp://localhost:8080/h2-console");
        log.info("Username: sa\t\tpassword: sa");
        log.info("******************************************************");
        log.info("Swagger UI:\thttp://localhost:8080/swagger-ui");
        log.info("******************************************************");
        log.info("*                Available users:                    *");
        log.info("******************************************************");
        userLoginRepository.findAll().forEach(it -> {
            log.info("Username: {}\t\tpassword: {}", it.getUsername(), "pass123");
        });
        log.info("******************************************************");

    }
}
