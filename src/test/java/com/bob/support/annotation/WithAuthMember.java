package com.bob.support.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import com.bob.support.auth.WithAuthMemberExtension;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD})
@ExtendWith(WithAuthMemberExtension.class)
public @interface WithAuthMember {

    String id() default "0199f8c2-30ed-7ee3-a757-16196412518c";
}
