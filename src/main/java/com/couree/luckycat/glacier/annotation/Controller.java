package com.couree.luckycat.glacier.annotation;

import com.couree.luckycat.glacier.Application;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation that indicates a class as controller. Note that classes annotated by {@link RestController }
 * provided by SpringBoot runs proceeding to {@link Application}, which leads to injection problem. To solve
 * this problem, adds {@link DependsOn} to ensure the {@link Application} initiated before the controller
 * classes.
 * @author James Chan
 */
@RestController
@DependsOn(Application.APPLICATION_COMPONENT_NAME)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
