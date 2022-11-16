package src.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import src.application.controller.request.UserRequest;
import src.domain.entity.User;
import src.domain.service.UserRegistryService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    public static final Long ID = 1L;
    public static final String NAME = "Axel";
    public static final String SURNAME = "Rose";
    public static final String PHONE = "5595980451966";
    public static final LocalDate DATE_BIRTH = LocalDate.of(1982, 2, 6);
    public static final String MARITAL_STATUS = "MARRIED";
    public static final String CITY = "Lafayette";
    public static final String STATE = "IN";
    public static final String CPF = "68799786060";
    public static final String OCCUPATION = "vocalist";
    private final String URL = "/users";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRegistryService registryService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void mustToRegistryUser() throws Exception {

        // cenary
        var userRequest = makeAnUserRequest();
        var userRequestJson = objectMapper.writeValueAsString(userRequest);

        var registeredUser = makeAnUser();

        doReturn(ID).when(registryService).registry(userRequest.toDomain());

        // action - validation
        mockMvc.perform(
                post(URL)
                        .content(userRequestJson)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());


    }

    private UserRequest makeAnUserRequest() {

        return UserRequest
                .builder()
                .name(NAME)
                .surname(SURNAME)
                .phone(PHONE)
                .dateBirth(DATE_BIRTH)
                .maritalStatus(MARITAL_STATUS)
                .city(CITY)
                .state(STATE)
                .cpf(CPF)
                .occupation(OCCUPATION)
                .build();

    }

    private User makeAnUser() {

        return User
                .builder()
                .id(ID)
                .name(NAME)
                .surname(SURNAME)
                .phone(PHONE)
                .dateBirth(DATE_BIRTH)
                .maritalStatus(MARITAL_STATUS)
                .city(CITY)
                .state(STATE)
                .cpf(CPF)
                .occupation(OCCUPATION)
                .build();

    }


}