package src.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class User {

    private Long id;
    private String name;
    private String surname;
    private String phone;
    private LocalDate dateBirth;
    private String maritalStatus;
    private String genre;
    private String city;
    private String state;
    private String cpf;
    private String occupation;

}
