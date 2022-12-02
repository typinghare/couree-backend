package com.couree.luckycat.glacier.annotation;

import com.couree.luckycat.glacier.constant.RegistryType;

import java.lang.annotation.*;

/**
 * Annotation that defines a metadata of a registry entry.
 * @author James Chan
 * @see ApplicationConfiguration
 * @see RegistryType
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Metadata {
    /**
     * Returns the key (or name) of a key-value pair.
     * @return the key (or name) of a key-value pair
     */
    String key();

    /**
     * Returns the type of the value.
     * @return the type of the value
     */
    String type() default RegistryType.STRING;

    /**
     * Returns the description of the key-value pair.
     * @return the description of the key-value pair
     */
    String description() default "";

    /**
     * Returns whether the value can be updated.
     * @return true if the value can be updated
     */
    boolean updatable() default true;
}
