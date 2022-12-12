package src.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import src.UserDatas;
import src.application.controller.request.UserRequest;
import src.domain.repository.UserRepository;
import src.domain.service.UserRegistryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private final String URL = "/users";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserRegistryService registryService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Must to Registry User")
    void mustToRegistryUser() throws Exception {

        // cenary
        var userID = UserDatas.ID;
        var userRequest = UserDatas.makeAnUserRequest();
        var userRequestJson = objectMapper.writeValueAsString(userRequest);
        var expectedLocationHeader = "http://localhost/users/" + userID;

        doReturn(userID).when(registryService).registry(userRequest.toDomain());

        // action - validation
        var mvcResult = mockMvc.perform(
                        post(URL)
                                .content(userRequestJson)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andReturn();


        String locationHeader = mvcResult.getResponse().getHeader("Location");

        assertEquals(expectedLocationHeader, locationHeader);

    }

    @Test
    @DisplayName("Don't should to Registry User with invalid datas")
    void dontShouldToRegistryUser() throws Exception {

        // cenary
        var userRequest = UserRequest.builder().build();
        var userRequestJson = objectMapper.writeValueAsString(userRequest);

        // action - validation
        mockMvc.perform(post(URL)
                        .content(userRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Validation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("/users"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.occupation").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.city").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.phone").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.surname").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.sex").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.sex").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.cpf").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.state").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.dateBirth").value("must not be null"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.maritalStatus").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.maritalStatus").exists());

    }

}