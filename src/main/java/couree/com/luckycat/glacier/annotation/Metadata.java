package couree.com.luckycat.glacier.annotation;

import couree.com.luckycat.glacier.constant.RegistryType;

import java.lang.annotation.*;

/**
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
