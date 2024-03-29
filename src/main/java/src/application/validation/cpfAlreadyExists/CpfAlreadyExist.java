package src.application.validation.cpfAlreadyExists;

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

    String message() default "{custom.annotation.cpf.already.exists.default.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
