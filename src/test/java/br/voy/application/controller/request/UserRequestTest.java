package br.voy.application.controller.request;

import br.voy.UserDatas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRequestTest {

    @Test
    @DisplayName("Must to convert UserRequest to UserDomain")
    void mustToConvertRequestToDomain() {

        // scenario
        var request = UserDatas.makeAnUserRequest();
        var expectedDomain = UserDatas.makeAnUserDomain();

        // action
        var domain = request.toDomain();

        // validation
        assertEquals(expectedDomain, domain);

    }

}