package src.application.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import src.domain.entity.User;

import java.time.LocalDate;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class UserResponse {

    @Schema(example = "Id")
    private Long id;

    @Schema(example = "Axel")
    private String name;

    @Schema(example = "Rose")
    private String surname;

    @Schema(example = "+5511938213160")
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateBirth;

    @Schema(type = "string", allowableValues = {"SINGLE", "MARRIED", "DIVORCED", "SEPARATED", "WIDOWED"})
    private String maritalStatus;

    @Schema(type = "string", allowableValues = {"MALE", "FEMALE", "DO_NOT_INFORM"})
    private String sex;

    @Schema(example = "Lafayette")
    private String city;

    @Schema(example = "IN")
    private String state;

    @Schema(example = "09776012850")
    private String cpf;

    @Schema(example = "Vocalist")
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
