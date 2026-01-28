package pl.przychodnia.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;

public class MaxBytesValidator implements ConstraintValidator<MaxBytes, String> {

    private int max;

    @Override
    public void initialize(MaxBytes constraintAnnotation) {
        this.max = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        int byteLength = value.getBytes(StandardCharsets.UTF_8).length;

        return byteLength <= max;
    }
}