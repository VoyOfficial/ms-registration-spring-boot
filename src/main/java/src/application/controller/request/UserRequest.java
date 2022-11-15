package src.application.controller.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;
import src.domain.entity.User;

import javax.validation.constraints.NotBlank;
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
    @Size(max = 9)
    private String maritalStatus;

    @NotBlank
    private String genre;

    @NotBlank
    @Size(max = 30)
    private String city;

    @NotBlank
    @Size(max = 2)
    private String state;

    @NotBlank
    @CPF
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
                .maritalStatus(maritalStatus)
                .genre(genre)
                .city(city)
                .state(state)
                .cpf(cpf)
                .occupation(occupation)
                .build();

    }
}