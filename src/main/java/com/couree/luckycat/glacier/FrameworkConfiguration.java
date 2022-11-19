package com.couree.luckycat.glacier;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@PropertySource("classpath:/application.properties")
@PropertySource("classpath:/.env.properties")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FrameworkConfiguration {
}
