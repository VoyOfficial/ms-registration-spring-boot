package src.infrastructure.repository.relational;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.UserDatas;
import src.domain.exception.InvalidUserException;
import src.infrastructure.model.UserModel;
import src.infrastructure.repository.jpa.UserJpaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        // scenario
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
    @DisplayName("Don't should to save an User when this is Invalid")
    void dontShouldToSaveAnUser() {

        // scenario
        var invalidUser = UserDatas.makeAnInvalidUserDomain();
        var expectedMessage = "invalid.user.default.message";

        // action
        var raisedException = assertThrows(InvalidUserException.class,
                () -> relationalRepository.saveUser(invalidUser)
        );

        // Validation
        assertEquals(InvalidUserException.class, raisedException.getClass());
        assertEquals(expectedMessage, raisedException.getMessage());

    }

    @Test
    @DisplayName("Must to Find an User by CPF")
    void mustToFindAnUserByCPF() {

        // scenario
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

        // scenario
        var userCpf = UserDatas.CPF;

        when(jpaRepository.findByCpf(userCpf)).thenReturn(Optional.empty());

        // action
        var optionalUser = relationalRepository.findByCpf(userCpf);

        // validation
        assertTrue(optionalUser.isEmpty());

    }

    @Test
    @DisplayName("Must to Find an User by ID")
    void mustToFindAnUserByID() {

        // scenario
        var userId = UserDatas.ID;
        var expectedOptionalUser = Optional.of(
                UserDatas.makeAnUserDomain(UserDatas.ID)
        );

        when(jpaRepository.findById(userId)).thenReturn(Optional.of(
                        new UserModel(expectedOptionalUser.get())
                )
        );

        // action
        var optionalUser = relationalRepository.findById(userId);

        // validation
        assertEquals(expectedOptionalUser, optionalUser);

    }

    @Test
    @DisplayName("Don't should to Find an User by ID")
    void dontShouldToFindAnUserByID() {

        // scenario
        var userId = UserDatas.ID;

        when(jpaRepository.findById(userId)).thenReturn(Optional.empty());

        // action
        var optionalUser = relationalRepository.findById(userId);

        // validation
        assertTrue(optionalUser.isEmpty());

    }

}