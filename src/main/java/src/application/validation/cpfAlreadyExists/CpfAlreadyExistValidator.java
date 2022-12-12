package src.application.validation.cpfAlreadyExists;

import src.domain.entity.User;
import src.domain.repository.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class CpfAlreadyExistValidator implements ConstraintValidator<CpfAlreadyExist, String> {

    UserRepository userRepository;

    public CpfAlreadyExistValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(CpfAlreadyExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cpfField, ConstraintValidatorContext constraintValidatorContext) {

        Optional<User> userExists = userRepository.findByCpf(cpfField);

        return userExists.isEmpty();

    }

}
