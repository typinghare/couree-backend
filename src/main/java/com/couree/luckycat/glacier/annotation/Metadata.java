package com.couree.luckycat.glacier.annotation;

import com.couree.luckycat.glacier.constant.RegistryType;

import java.lang.annotation.*;

/**
 * Annotation that defines a metadata of a registry entry.
 * @see ApplicationConfiguration
 * @see RegistryType
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Metadata {
    String key();

    String type() default RegistryType.STRING;

    String description() default "";

    boolean updatable() default true;
}
