package project.tdlogistics.users.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;

@Component
public class ValidationService {

    private final Validator validator;

    @Autowired
    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    public <T> void validateRequest(T request, Class<?> group) throws MethodArgumentNotValidException {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(request, group);
        List<ObjectError> errors = new ArrayList<>();
        for (ConstraintViolation<T> violation : constraintViolations) {
            errors.add(new FieldError(violation.getRootBeanClass().getName(), violation.getPropertyPath().toString(), violation.getMessage()));
        }

        System.out.println(errors);
        if (!errors.isEmpty()) {
            throw new MethodArgumentNotValidException(null, (BindingResult) errors);
        }
    }
}
