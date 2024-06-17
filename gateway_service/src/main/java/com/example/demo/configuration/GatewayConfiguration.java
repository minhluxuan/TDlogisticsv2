package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@Configuration
@EnableHystrix
public class GatewayConfiguration {

    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public RouteLocator routerBuilder(RouteLocatorBuilder routeLocatorBuilder){ 
        return routeLocatorBuilder.routes() 
            .route("auth-service", r -> r.path("/v2/auth/**", "/v2/customers/**", "/v2/staffs/**")
                .filters(f -> f.filter(filter))
                .uri("lb://users-service"))
            .route("users-service", r -> r.path("/v2/users/**")
                .filters(f -> f.filter(filter))
                .uri("lb://users-service"))
            .route("websocket-service", r -> r.path("/socket.io/**")
                // .filters(f -> f.rewritePath("/socket.io/(?<remaining>.*)", "/${remaining}"))
                .uri("ws://localhost:8085"))
            .build();
    }
}
