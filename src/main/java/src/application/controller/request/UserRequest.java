package src.application.controller.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;
import src.application.validation.cpfAlreadyExists.CpfAlreadyExist;
import src.application.validation.valueOfEnum.ValueOfEnum;
import src.domain.entity.User;
import src.infrastructure.model.enums.MaritalStatusEnum;
import src.infrastructure.model.enums.SexEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Builder
public class UserRequest extends AbstractRequest {

    @Schema(example = "Axel")
    @NotBlank
    @Size(min = 3, max = 30)
    private String name;

    @Schema(example = "Rose")
    @NotBlank
    @Size(min = 3, max = 50)
    private String surname;

    @Schema(example = "+5511938213160")
    @NotBlank
    @Size(min = 11, max = 14)
    private String phone;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateBirth;

    @Schema(type = "string", allowableValues = {"SINGLE", "MARRIED", "DIVORCED", "SEPARATED", "WIDOWED"})
    @NotBlank
    @ValueOfEnum(enumClass = MaritalStatusEnum.class, message = "{user.marital.status.message.invalid}")
    private String maritalStatus;

    @Schema(type = "string", allowableValues = {"MALE", "FEMALE", "DO_NOT_INFORM"})
    @NotEmpty
    @ValueOfEnum(enumClass = SexEnum.class, message = "{user.sex.message.invalid}")
    private String sex;

    @Schema(example = "Lafayette")
    @NotBlank
    @Size(max = 30)
    private String city;

    @Schema(example = "IN")
    @NotBlank
    @Size(max = 2)
    private String state;

    @Schema(example = "09776012850")
    @CPF
    @NotBlank
    @CpfAlreadyExist(message = "{user.cpf.field.message.already.exists}")
    private String cpf;

    @Schema(example = "Vocalist")
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