package com.moon.tinybilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author Chanmoey
 * @date 2022年07月11日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface ApiLimited {

    String[] limitedRoleCodeList() default {};
}
