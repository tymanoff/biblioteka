package com.edu.ulab.app.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.edu.ulab.app.entity")
@EnableJpaRepositories(basePackages = {"com.edu.ulab.app.repository"})
@ComponentScan({"com.edu.ulab.app.repository"})
public class SystemTestingJpaConfig {
}
