package src.infrastructure.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.UserDatas;

class UserModelTest {

    @Test
    @DisplayName("Must to convert UserDomain to UserModel")
    void mustToConvertDomainToModel() {


        // cenary
        var domain = UserDatas.makeAnUserDomain();
        var expectedModel = new UserModel(domain);

        // action
        var model = new UserModel(domain);

        // validation
        Assertions.assertEquals(expectedModel, model);

    }

    @Test
    @DisplayName("Must to convert UserModel to UserDomain")
    void mustToConvertModelToDomain() {


        // cenary
        var model = UserDatas.makeAnUserModel();
        var expectedDomain = UserDatas.makeAnUserDomain();

        // action
        var domain = model.toDomain();

        // validation
        Assertions.assertEquals(expectedDomain, domain);

    }

}