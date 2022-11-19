package couree.com.luckycat.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author James Chan
 */
@Configuration
@PropertySource("classpath:/.env.properties")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CoreConfiguration {
}
