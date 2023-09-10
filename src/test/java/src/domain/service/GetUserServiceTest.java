package src.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.UserDatas;
import src.domain.exception.UserNotFoundException;
import src.domain.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    GetUserService service;

    @Test
    @DisplayName("Must to Get User By Id")
    void mustToGetUserById() {

        // scenario
        var userId = UserDatas.ID;
        var expectedUser = Optional.of(UserDatas.makeAnUserDomain(userId));

        when(repository.findById(userId)).thenReturn(expectedUser);

        // action
        var userfound = service.getUserById(userId);

        // validation
        assertEquals(expectedUser.get(), userfound);

    }

    @Test
    @DisplayName("Don't to Get User By Id when not exists")
    void dontToGetUserByIdWhenNotExists() {

        // scenario
        var userId = UserDatas.ID;
        var expectedException = new UserNotFoundException();

        // action
        var raisedException = assertThrows(UserNotFoundException.class,
                () -> service.getUserById(userId)
        );

        // validation
        assertEquals(expectedException.getClass(), raisedException.getClass());
        assertEquals(expectedException.getMessage(), raisedException.getMessage());

    }

}