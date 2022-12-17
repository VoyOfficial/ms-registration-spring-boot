package src.infrastructure.repository.relational;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.UserDatas;
import src.domain.entity.User;
import src.domain.exception.InvalidUserException;
import src.infrastructure.model.UserModel;
import src.infrastructure.repository.jpa.UserJpaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelationalUserRepositoryTest {

    @InjectMocks
    RelationalUserRepository relationalRepository;

    @Mock
    UserJpaRepository jpaRepository;

    @Test
    @DisplayName("Must to save an User")
    void mustToSaveAnUser() {

        // cenary
        var expectedUserId = UserDatas.ID;
        var userDomain = UserDatas.makeAnUserDomain(expectedUserId);

        var userModel = new UserModel(userDomain);

        var expectedUserModel = UserDatas.makeAnUserModel(expectedUserId);

        when(jpaRepository.save(userModel)).thenReturn(expectedUserModel);

        // action
        var savedUser = relationalRepository.saveUser(userDomain);

        // validation
        assertEquals(userDomain, savedUser);

        verify(jpaRepository, times(1)).save(any(UserModel.class));

    }

    @Test
    @DisplayName("Don't to save an User")
    void dontShouldToSaveAnUser() {

        // cenary
        var invalidUser = UserDatas.makeAnInvalidUserDomain();

        // action
        var raisedException = Assertions.assertThrows(InvalidUserException.class,
                () -> relationalRepository.saveUser(invalidUser)
        );

        // Validation
        assertEquals(InvalidUserException.class, raisedException.getClass());
        assertEquals("{invalid.user.default.message}", raisedException.getMessage());

    }

    @Test
    @DisplayName("Must to Find an User by CPF")
    void mustToFindAnUserByCPF() {

        // cenary
        var userCpf = UserDatas.CPF;
        var expectedOptionalUser = Optional.of(
                UserDatas.makeAnUserDomain(UserDatas.ID)
        );

        when(jpaRepository.findByCpf(userCpf)).thenReturn(Optional.of(
                        new UserModel(expectedOptionalUser.get())
                )
        );

        // action
        var optionalUser = relationalRepository.findByCpf(userCpf);

        // validation
        assertEquals(expectedOptionalUser, optionalUser);

    }

    @Test
    @DisplayName("Don't should to Find an User by CPF")
    void dontShouldToFindAnUserByCPF() {

        // cenary
        var userCpf = UserDatas.CPF;

        when(jpaRepository.findByCpf(userCpf)).thenReturn(Optional.empty());

        // action
        var optionalUser = relationalRepository.findByCpf(userCpf);

        // validation
        assertTrue(optionalUser.isEmpty());

    }

}