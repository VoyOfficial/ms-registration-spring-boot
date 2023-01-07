package src.application.controller.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.UserDatas;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserResponseTest {

    @Test
    @DisplayName("Must to convert User to UserResponse")
    void mustToConvertUserToUserResponse() {

        // cenary
        var domain = UserDatas.makeAnUserDomain();
        var expectedResponse = UserDatas.makeAnUserResponse(domain);

        // action
        var response = new UserResponse(domain);

        // validation
        assertEquals(expectedResponse, response);

    }

}