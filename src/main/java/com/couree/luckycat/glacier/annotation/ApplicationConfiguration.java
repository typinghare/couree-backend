package com.couree.luckycat.glacier.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates a glacier application.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
@Documented
public @interface ApplicationConfiguration {
    /**
     * Returns the name of this application. Note that applications with identical names is not allowed.
     * @return the name of this application
     */
    String name();

    /**
     * Return the dependencies of this application.
     * @return the dependencies of this application
     */
    String[] dependency() default {};

    /**
     * Returns the metadata list.
     * @return the metadata list
     */
    Metadata[] metadata() default {};
}
