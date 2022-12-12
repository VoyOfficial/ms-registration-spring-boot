package src.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.UserDatas;
import src.domain.entity.User;
import src.domain.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistryServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserRegistryService service;

    @Test
    @DisplayName("Service - Must to Registry an User")
    void mustToRegistryAnUser(){

        // cenary
        var expectedUserId = UserDatas.ID;
        var userDomain = UserDatas.makeAnUserDomain(expectedUserId);

        when(repository.saveUser(userDomain)).thenReturn(userDomain);

        // action
        var userId = service.registry(userDomain);

        // validation
        assertEquals(expectedUserId, userId);

        verify(repository, times(1)).saveUser(any(User.class));

    }


}