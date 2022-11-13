package couree.com.luckycat.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryEntries {
    RegistryEntry[] value();
}
