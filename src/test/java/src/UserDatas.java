package src;

import src.application.controller.request.UserRequest;
import src.application.controller.response.UserResponse;
import src.domain.entity.User;
import src.infrastructure.model.UserModel;
import src.infrastructure.model.enums.MaritalStatusEnum;
import src.infrastructure.model.enums.SexEnum;

import java.time.LocalDate;

public class UserDatas {

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

    public static User makeAnUserDomain() {

        return User
                .builder()
                .id(null)
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

    public static User makeAnUserDomain(Long id) {

        return User
                .builder()
                .id(id)
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

    public static User makeAnInvalidUserDomain() {

        return User
                .builder()
                .build();

    }

    public static UserModel makeAnUserModel() {

        return UserModel
                .builder()
                .id(null)
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

    public static UserModel makeAnUserModel(Long id) {

        return UserModel
                .builder()
                .id(id)
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

    public static UserRequest makeAnUserRequest() {

        return UserRequest
                .builder()
                .name(NAME)
                .surname(SURNAME)
                .phone(PHONE)
                .dateBirth(DATE_BIRTH)
                .maritalStatus(MARITAL_STATUS)
                .sex(SexEnum.MALE.name())
                .city(CITY)
                .state(STATE)
                .cpf(CPF)
                .occupation(OCCUPATION)
                .build();

    }

    public static UserRequest makeAnUserRequestWithInvalidMaritalStatus() {

        return UserRequest
                .builder()
                .name(NAME)
                .surname(SURNAME)
                .phone(PHONE)
                .dateBirth(DATE_BIRTH)
                .maritalStatus("marrieded")
                .sex(SexEnum.MALE.name())
                .city(CITY)
                .state(STATE)
                .cpf(CPF)
                .occupation(OCCUPATION)
                .build();

    }

    public static UserResponse makeAnUserResponse() {

        return UserResponse
                .builder()
                .id(ID)
                .name(NAME)
                .surname(SURNAME)
                .phone(PHONE)
                .dateBirth(DATE_BIRTH)
                .maritalStatus(MARITAL_STATUS)
                .sex(SexEnum.MALE.name())
                .city(CITY)
                .state(STATE)
                .cpf(CPF)
                .occupation(OCCUPATION)
                .build();

    }

    public static UserResponse makeAnUserResponse(User userDomain) {

        return new UserResponse(userDomain);

    }

}
