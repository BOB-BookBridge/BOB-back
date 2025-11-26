package com.bob.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bob.support.config.BobTestConfiguration;
import com.bob.support.container.TestContainersInitializer;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = TestContainersInitializer.class)
@Import(BobTestConfiguration.class)
@SpringBootTest
@Transactional
public @interface ContainerTest {

}
