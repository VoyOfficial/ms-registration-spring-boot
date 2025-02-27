package br.voy.application.controller.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import br.voy.UserDatas;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserResponseTest {

    @Test
    @DisplayName("Must to convert User to UserResponse")
    void mustToConvertUserToUserResponse() {

        // scenario
        var domain = UserDatas.makeAnUserDomain();
        var expectedResponse = UserDatas.makeAnUserResponse(domain);

        // action
        var response = new UserResponse(domain);

        // validation
        assertEquals(expectedResponse, response);

    }

}