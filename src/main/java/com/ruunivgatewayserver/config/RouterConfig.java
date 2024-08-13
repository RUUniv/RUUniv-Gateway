package com.ruunivgatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/verification-api/**")
                        .filters(f -> f.rewritePath("/verification-api/(?<segment>.*)", "/${segment}"))
                        .uri("lb://RUUNIV-VERFICATION-SERVER"))
                .route(r -> r.path("/statistics-api/**")
                        .filters(f -> f.rewritePath("/statistics-api/(?<segment>.*)", "/${segment}"))
                        .uri("lb://RUUNIV-STATISTICS-SERVER"))
                .build();
    }
}