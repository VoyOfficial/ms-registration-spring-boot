package src.application.controller.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;
import src.application.validation.uniqueValue.UniqueValue;
import src.application.validation.valueOfEnum.ValueOfEnum;
import src.infrastructure.model.UserModel;
import src.infrastructure.model.enums.MaritalStatusEnum;
import src.infrastructure.model.enums.SexEnum;
import src.domain.entity.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Builder
public class UserRequest extends AbstractRequest {

    @NotBlank
    @Size(min = 3, max = 30)
    private String name;

    @NotBlank
    @Size(min = 3, max = 50)
    private String surname;

    @NotBlank
    @Size(min = 11, max = 14)
    private String phone;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateBirth;

    @NotBlank
    @ValueOfEnum(enumClass = MaritalStatusEnum.class)
    private String maritalStatus;

    @NotEmpty
    @ValueOfEnum(enumClass = SexEnum.class)
    private String sex;

    @NotBlank
    @Size(max = 30)
    private String city;

    @NotBlank
    @Size(max = 2)
    private String state;

    @CPF
    @NotBlank
    @UniqueValue(modelClass = UserModel.class, field = "cpf", message = "exists")
    private String cpf;

    @NotBlank
    @Size(max = 30)
    private String occupation;

    @Override
    public User toDomain() {

        return User
                .builder()
                .id(null)
                .name(name)
                .surname(surname)
                .phone(phone)
                .dateBirth(dateBirth)
                .maritalStatus(MaritalStatusEnum.valueOf(maritalStatus.toUpperCase()))
                .sex(SexEnum.valueOf(sex.toUpperCase()))
                .city(city)
                .state(state)
                .cpf(cpf)
                .occupation(occupation)
                .build();

    }
}