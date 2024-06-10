package com.example.demo.configuration;

import org.springframework.http.HttpCookie;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPatternParser;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
import com.example.demo.models.ReponseObject;
import com.example.demo.models.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RefreshScope
@Component
@Configuration
public class AuthenticationFilter implements GatewayFilter {
    @Autowired
    private RouterValidator routerValidator;

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (routerValidator.isSecured.test(request)) {
            if (this.isAuthMissing(request))
                return this.onError(exchange, "Vui lòng đăng nhập", HttpStatus.UNAUTHORIZED);

            final String token = this.getToken(request);

            if (jwtUtil.isTokenInvalid(token))
                return this.onError(exchange, "Token không hợp lệ", HttpStatus.UNAUTHORIZED);

            this.populateRequestWithHeaders(exchange, token);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        @SuppressWarnings("rawtypes")
        ReponseObject responseObject = new ReponseObject<>(true, err, null);
        DataBuffer buffer;
        try {
            buffer = exchange.getResponse().bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(responseObject));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    @SuppressWarnings("null")
    private String getToken(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        if (headers.containsKey("Authorization")) {
            List<String> authHeaders = headers.get("Authorization");
            return authHeaders.get(0);
        }

        HttpCookie sessionCookie = request.getCookies().getFirst("JSESSIONID");
        if (sessionCookie != null) {
            return sessionCookie.getValue();
        }

        return null;
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization") && request.getCookies().getFirst("JSESSIONID") == null;
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
        .headers(httpHeaders -> {
            httpHeaders.set("role", String.valueOf(claims.get("role")));
            httpHeaders.set("userId", String.valueOf(claims.get("userId")));
            httpHeaders.set("agencyId", String.valueOf(claims.get("agencyId")));
        })
        .build();
        exchange.mutate().request(mutatedRequest).build();
    }
}
