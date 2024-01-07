package com.example.empay.config;

import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!cli")
public class QuartzConfiguration extends QuartzAutoConfiguration { }
