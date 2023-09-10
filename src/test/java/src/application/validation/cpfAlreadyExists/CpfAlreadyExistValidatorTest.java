package src.application.validation.cpfAlreadyExists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.UserDatas;
import src.domain.repository.UserRepository;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CpfAlreadyExistValidatorTest {

    @Mock
    ConstraintValidatorContext context;

    @Mock
    UserRepository userRepository;

    CpfAlreadyExistValidator cpfAlreadyExistValidator;

    @BeforeEach
    void setUp() {
        this.cpfAlreadyExistValidator = new CpfAlreadyExistValidator(userRepository);
        this.cpfAlreadyExistValidator.initialize(createCpfAlreadyExistsAnnotation());
    }

    private CpfAlreadyExist createCpfAlreadyExistsAnnotation() {

        return new CpfAlreadyExist() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }
        };

    }

    @Test
    @DisplayName("Must check if given cpf already exist and is it Valid - True")
    void mustToCheckIfGivenCpfAlreadyExistAndIsItValidTrue() {

        // scenario
        var cpf = UserDatas.CPF;

        when(userRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // action
        var cpfIsValid = cpfAlreadyExistValidator.isValid(cpf, context);

        // validation
        assertTrue(cpfIsValid);

    }

    @Test
    @DisplayName("Must check if given cpf already exist and is it Valid - False")
    void mustToCheckIfGivenCpfAlreadyExistAndReturnFalse() {

        // scenario
        var cpf = UserDatas.CPF;

        when(userRepository.findByCpf(cpf)).thenReturn(
                Optional.of(UserDatas.makeAnUserDomain(1L))
        );

        // action
        var cpfIsValid = cpfAlreadyExistValidator.isValid(cpf, context);

        // validation
        assertFalse(cpfIsValid);

    }

}