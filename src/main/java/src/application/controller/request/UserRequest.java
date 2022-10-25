package src.application.controller.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
public class UserRequest {

    @NotBlank
    @Size(min = 3, max= 30)
    private String name;

    @NotBlank
    @Size(min = 3, max = 50)
    private String surname;

    @NotBlank
    @Size(min = 11, max = 14)
    private String phone;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
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
//    @Size(min = 11, max = 11)
    private String cpf;

    @NotBlank
    @Size(max = 30)
    private String occupation;

}