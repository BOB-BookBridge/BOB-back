package com.bob.support;

import static com.bob.support.mysql.MySQLContainerProvider.MYSQL_CONTAINER;
import static com.bob.support.redis.RedisContainerProvider.REDIS_CONTAINER;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class TestContainerSupport {

  @DynamicPropertySource
  static void registerDynamicProperties(DynamicPropertyRegistry registry) {
    // MySQL
    registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
    registry.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);

    // Redis
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.password", () -> "password");
    registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
  }
}
