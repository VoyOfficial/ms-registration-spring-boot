package br.voy.application.controller.request;

import br.voy.application.validation.cpfAlreadyExists.CpfAlreadyExist;
import br.voy.application.validation.valueOfEnum.ValueOfEnum;
import br.voy.domain.entity.User;
import br.voy.domain.entity.enums.MaritalStatusEnum;
import br.voy.domain.entity.enums.SexEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Builder
@Schema(name = "UserRequest", description = "Payload to register a new user")
public class UserRequest extends AbstractRequest {

    @Schema(description = "First name", example = "Axel")
    @NotBlank
    @Size(min = 3, max = 30)
    private String name;

    @Schema(description = "Last name", example = "Rose")
    @NotBlank
    @Size(min = 3, max = 50)
    private String surname;

    @Schema(description = "Phone in E.164-like format", example = "+5511938213160")
    @NotBlank
    @Size(min = 11, max = 14)
    private String phone;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Date of birth", example = "1990-05-20", type = "string", format = "date")
    private LocalDate dateBirth;

    @Schema(
            description = "Marital status",
            type = "string",
            allowableValues = {"SINGLE", "MARRIED", "DIVORCED", "SEPARATED", "WIDOWED"},
            example = "SINGLE")
    @NotBlank
    @ValueOfEnum(
            enumClass = MaritalStatusEnum.class,
            message = "{user.marital.status.message.invalid}")
    private String maritalStatus;

    @Schema(
            description = "Sex",
            type = "string",
            allowableValues = {"MALE", "FEMALE", "DO_NOT_INFORM"},
            example = "MALE")
    @NotEmpty
    @ValueOfEnum(enumClass = SexEnum.class, message = "{user.sex.message.invalid}")
    private String sex;

    @Schema(description = "City", example = "Lafayette")
    @NotBlank
    @Size(max = 30)
    private String city;

    @Schema(description = "State UF / code", example = "IN")
    @NotBlank
    @Size(max = 2)
    private String state;

    @Schema(description = "CPF", example = "09776012850")
    @CPF
    @NotBlank
    @CpfAlreadyExist(message = "{user.cpf.field.message.already.exists}")
    private String cpf;

    @Schema(description = "Occupation", example = "Vocalist")
    @NotBlank
    @Size(max = 30)
    private String occupation;

    @Override
    public User toDomain() {

        return User.builder()
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
