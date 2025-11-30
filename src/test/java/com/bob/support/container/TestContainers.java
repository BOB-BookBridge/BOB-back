package com.bob.support.container;

import java.util.stream.Stream;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

public class TestContainers {

    public static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.36"))
        .withDatabaseName("test")
        .withUsername("user")
        .withPassword("password")
        .withInitScript("sql/schema.sql");

    public static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2.4"))
        .withExposedPorts(6379)
        .waitingFor(Wait.forListeningPort());

    static {
        Startables.deepStart(Stream.of(MYSQL, REDIS)).join();
        System.setProperty("spring.data.redis.host", REDIS.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(REDIS.getMappedPort(6379)));
        System.setProperty("spring.data.redis.password", "password");
    }
}
