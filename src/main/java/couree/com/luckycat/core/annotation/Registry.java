package couree.com.luckycat.core.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public @interface Registry {
    int value() default Ordered.LOWEST_PRECEDENCE;
}
