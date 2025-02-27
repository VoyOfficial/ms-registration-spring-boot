package br.voy.application.controller.response;

import br.voy.UserDatas;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals(expectedResponse, response);

    }

}