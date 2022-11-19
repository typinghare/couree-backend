package couree.com.luckycat.core.annotation;

import java.lang.annotation.*;

/**
 * Initializer.
 * @author James Chan
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Initializer {
}
