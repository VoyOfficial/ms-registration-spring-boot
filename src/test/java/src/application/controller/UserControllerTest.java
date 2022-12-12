package src.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import src.application.controller.request.UserRequest;
import src.domain.entity.User;
import src.domain.service.UserRegistryService;
import src.infrastructure.model.enums.MaritalStatusEnum;
import src.infrastructure.model.enums.SexEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
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
    public static final String SEX = "MALE";
    public static final String CITY = "Lafayette";
    public static final String STATE = "IN";
    public static final String CPF = "68799786060";
    public static final String OCCUPATION = "vocalist";
    private final String URL = "/users";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EntityManager entityManager;

//    @MockBean
//    UniqueValueValidator validator;

    @MockBean
    UserRegistryService registryService;

    @Autowired
    ObjectMapper objectMapper;

    // TODO To Resolve
    @Disabled
    @Test
    @DisplayName("Must to Registry User")
    void mustToRegistryUser() throws Exception {

        // cenary
        var userRequest = makeAnUserRequest();
        var userRequestJson = objectMapper.writeValueAsString(userRequest);

        var registeredUser = makeAnUser();

        var sql = "select 1 from user where cpf = :value ";
        var query = mock(Query.class);
        var results = new ArrayList<>();

        when(entityManager.createQuery(sql)).thenReturn(query);
        when(query.getResultList()).thenReturn(results);

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
                .sex(SEX)
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
                .maritalStatus(MaritalStatusEnum.valueOf(MARITAL_STATUS))
                .sex(SexEnum.valueOf(SEX))
                .city(CITY)
                .state(STATE)
                .cpf(CPF)
                .occupation(OCCUPATION)
                .build();

    }


}