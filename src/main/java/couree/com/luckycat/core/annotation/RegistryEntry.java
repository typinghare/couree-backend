package couree.com.luckycat.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Registry entry.
 * @author James Chan
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RegistryEntries.class)
@Component
public @interface RegistryEntry {
    /**
     * Returns the key of this entry.
     * @return key of this entry
     */
    String key();

    /**
     * Returns the value of this entry.
     * @return value of this entry
     */
    String value() default "";
}
