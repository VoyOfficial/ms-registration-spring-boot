package br.voy.application.validation.cpfAlreadyExists;

import br.voy.domain.entity.User;
import br.voy.domain.repository.UserRepository;
import java.util.Optional;
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

        Optional<User> userExists = userRepository.findByCpf(cpfField);

        return userExists.isEmpty();
    }
}
