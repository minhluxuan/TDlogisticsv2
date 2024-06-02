package project.tdlogistics.agency_company.configurations;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfiguration {
    @Bean
    public Validator validator() {
        try {
            return Validation.buildDefaultValidatorFactory().getValidator();
        } catch (Exception e) {
            return null;
        }
    }
}

