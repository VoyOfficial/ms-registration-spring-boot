package src.domain.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import src.infrastructure.model.enums.MaritalStatusEnum;
import src.infrastructure.model.enums.SexEnum;

import java.time.LocalDate;

@Getter
@Builder
@EqualsAndHashCode
public class User {

    private Long id;
    private String name;
    private String surname;
    private String phone;
    private LocalDate dateBirth;
    private MaritalStatusEnum maritalStatus;
    private SexEnum sex;
    private String city;
    private String state;
    private String cpf;
    private String occupation;

}
