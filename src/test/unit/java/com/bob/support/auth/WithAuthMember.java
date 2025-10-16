package com.bob.support.auth;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import com.bob.support.auth.extension.WithAuthMemberExtension;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD})
@ExtendWith(WithAuthMemberExtension.class)
public @interface WithAuthMember {

  String id() default "00000000-0000-0000-0000-000000000000";
}
