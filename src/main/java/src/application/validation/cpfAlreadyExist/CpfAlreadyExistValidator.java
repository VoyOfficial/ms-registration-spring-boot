package src.application.validation.cpfAlreadyExist;

import src.domain.repository.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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

        var userExists = userRepository.findByCpf(cpfField);

        return userExists.isEmpty();

    }

}
