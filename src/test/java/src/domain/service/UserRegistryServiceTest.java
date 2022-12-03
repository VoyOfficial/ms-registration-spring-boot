package src.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import src.UserDatas;
import src.domain.entity.User;
import src.domain.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        var expectedUserId = 1L;
        var userDomain = UserDatas.makeAnUserDomain(1L);

        Mockito.when(repository.saveUser(userDomain)).thenReturn(userDomain);

        // action
        var userId = service.registry(userDomain);

        // validation
        assertEquals(expectedUserId, userId);

        verify(repository, times(1)).saveUser(Mockito.any(User.class));

    }


}