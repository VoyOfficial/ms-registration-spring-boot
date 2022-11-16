package src.application.validation.uniqueValue;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UniqueValueValidator.class)
public @interface UniqueValue {

    Class<?> modelClass();

    String field();

    String message() default "{custom_annotation.unique_value.message.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
