package project.tdlogistics.users.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

@Component
public class ValidationService {

    private final Validator validator;

    public ValidationService() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public <T> void validateRequest(T request, Class<?> group) throws BindException {
        // Validate using the javax.validation.Validator
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(request, group);

        // Convert ConstraintViolation to Spring's BindingResult
        BindingResult errors = new org.springframework.validation.BeanPropertyBindingResult(request, request.getClass().getName());

        for (ConstraintViolation<T> violation : constraintViolations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.rejectValue(field, null, message);
        }

        // If there are validation errors, throw BindException (subclass of MethodArgumentNotValidException)
        if (errors.hasErrors()) {
            throw new org.springframework.validation.BindException(errors);
        }
    }
}

