package com.bob;

import com.bob.support.TestContainerSupport;
import com.bob.support.redis.RedisContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


@Import(RedisContainerConfig.class)
@SpringBootTest
class BookBridgeApplicationTest extends TestContainerSupport {

  @Test
  void contextLoads() {

  }
}