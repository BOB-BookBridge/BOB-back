package com.bob.support.container;

import static com.bob.support.container.TestContainers.MYSQL;
import static com.bob.support.container.TestContainers.REDIS;

import java.util.stream.Stream;

import org.testcontainers.lifecycle.Startables;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class TestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static boolean dbInitialized = false;

    static {
        Startables.deepStart(Stream.of(MYSQL, REDIS)).join();
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        String ddlAuto = dbInitialized ? "none" : "update";

        TestPropertyValues.of(
            "spring.datasource.url=" + MYSQL.getJdbcUrl(),
            "spring.datasource.username=" + MYSQL.getUsername(),
            "spring.datasource.password=" + MYSQL.getPassword(),
            "spring.datasource.driver-class-name=" + MYSQL.getDriverClassName(),

            "spring.jpa.hibernate.ddl-auto=" + ddlAuto,
            "spring.sql.init.mode=" + (dbInitialized ? "never" : "always")
        ).applyTo(context.getEnvironment());

        dbInitialized = true;
    }
}
