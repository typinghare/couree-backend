package couree.com.luckycat.glacier.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates an application.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface ApplicationConfiguration {
    /**
     * Returns the name of the application. Note that applications with identical names is not allowed.
     * @return the name of the application
     */
    String name();

    Metadata[] metadata();
}
