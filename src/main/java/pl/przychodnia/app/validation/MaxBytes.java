package pl.przychodnia.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MaxBytesValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxBytes {

    String message() default "Przekroczono limit danych (polskie znaki zajmują więcej miejsca)";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int value();
}