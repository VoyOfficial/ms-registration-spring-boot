package br.voy.application.controller.response;

import br.voy.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@Schema(name = "UserResponse", description = "Registered user profile")
public class UserResponse {

    @Schema(description = "User id", example = "1")
    private Long id;

    @Schema(description = "First name", example = "Axel")
    private String name;

    @Schema(description = "Last name", example = "Rose")
    private String surname;

    @Schema(description = "Phone in E.164-like format", example = "+5511938213160")
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Date of birth", example = "1990-05-20", type = "string", format = "date")
    private LocalDate dateBirth;

    @Schema(description = "Persisted marital status label", example = "SINGLE")
    private String maritalStatus;

    @Schema(description = "Persisted sex label", example = "MALE")
    private String sex;

    @Schema(description = "City", example = "Lafayette")
    private String city;

    @Schema(description = "State UF / code", example = "IN")
    private String state;

    @Schema(description = "CPF", example = "09776012850")
    private String cpf;

    @Schema(description = "Occupation", example = "Vocalist")
    private String occupation;

    public UserResponse(User domain) {

        this.id = domain.getId();
        this.name = domain.getName();
        this.surname = domain.getSurname();
        this.phone = domain.getPhone();
        this.dateBirth = domain.getDateBirth();
        this.maritalStatus = domain.getMaritalStatus().name();
        this.sex = domain.getSex().name();
        this.city = domain.getCity();
        this.state = domain.getState();
        this.cpf = domain.getCpf();
        this.occupation = domain.getOccupation();
    }
}
