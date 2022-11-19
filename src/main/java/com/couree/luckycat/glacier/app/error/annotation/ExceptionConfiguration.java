package com.couree.luckycat.glacier.app.error.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ExceptionConfiguration {
    /**
     * Returns the exception code of the annotated exception class.
     * @return the exception code of the annotated exception class.
     */
    int exceptionCode();
}
