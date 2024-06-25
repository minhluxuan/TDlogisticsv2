package project.tdlogistics.users.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrEmptyValidator implements ConstraintValidator<NullOrEmpty, Object> {
    @Override
    public void initialize(NullOrEmpty constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value == null;
    }
}

