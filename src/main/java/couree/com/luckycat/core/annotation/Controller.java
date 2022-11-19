package couree.com.luckycat.core.annotation;

import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation that indicates a class as controller. Note that classes annotated by {@link RestController } provided by
 * SpringBoot runs proceeding to {@link couree.com.luckycat.core.Config}, which leads to injection problem. To solve
 * this problem, adds {@link DependsOn} to ensure the {@link couree.com.luckycat.core.Config} initiated before the
 * controller classes.
 * @author James Chan
 */
@RestController
@DependsOn("config")
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
