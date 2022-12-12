package src.infrastructure.repository.relational;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import src.UserDatas;
import src.infrastructure.model.UserModel;
import src.infrastructure.repository.jpa.UserJpaRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}