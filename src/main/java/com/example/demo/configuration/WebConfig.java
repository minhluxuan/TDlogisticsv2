package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://127.0.0.1:5500")  // Allow all origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}

// // package com.example.demo.configuration;

// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.web.cors.CorsConfiguration;
// // import org.springframework.web.cors.CorsConfigurationSource;
// // import org.springframework.web.cors.reactive.CorsWebFilter;
// // import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
// // import org.springframework.web.util.pattern.PathPatternParser;

// // import java.util.Arrays;

// // @Configuration
// // public class CorsConfig {

// //     @Bean
// //     public CorsConfigurationSource corsConfigurationSource() {
// //         CorsConfiguration configuration = new CorsConfiguration();
// //         configuration.setAllowedOrigins(Arrays.asList("*")); // Change "*" to specific origins if needed
// //         configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
// //         configuration.setAllowedHeaders(Arrays.asList("*"));
// //         configuration.setAllowCredentials(true);

// //         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
// //         source.registerCorsConfiguration("/**", configuration);
// //         return (CorsConfigurationSource) source;
// //     }

// //     @Bean
// //     public CorsWebFilter corsWebFilter() {
// //         return new CorsWebFilter((org.springframework.web.cors.reactive.CorsConfigurationSource) corsConfigurationSource());
// //     }
// // }


