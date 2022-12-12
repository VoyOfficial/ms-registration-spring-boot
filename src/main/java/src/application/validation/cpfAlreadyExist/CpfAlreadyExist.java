package src.application.validation.cpfAlreadyExist;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CpfAlreadyExistValidator.class)
public @interface CpfAlreadyExist {

    String message() default "{custom_annotation.cpf.already.exists.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
