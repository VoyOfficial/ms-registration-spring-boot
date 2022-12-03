package src.infrastructure.model;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import src.domain.entity.User;
import src.infrastructure.model.enums.MaritalStatusEnum;
import src.infrastructure.model.enums.SexEnum;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "user")
@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "marital_status_enum", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "sex_enum", typeClass = PostgreSQLEnumType.class)
public class UserModel extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private LocalDate dateBirth;

    @Enumerated(EnumType.STRING)
    @Type(type = "sex_enum")
    @Column(name = "sex", nullable = false)
    private SexEnum sex;
    @Enumerated(EnumType.STRING)
    @Type(type = "marital_status_enum")
    @Column(name = "marital_status", nullable = false)
    private MaritalStatusEnum maritalStatus;
    private String city;
    private String state;
    private String cpf;
    private String occupation;

    public UserModel(User userDomain) {

        this.id = userDomain.getId();
        this.name = userDomain.getName();
        this.surname = userDomain.getSurname();
        this.phone = userDomain.getPhone();
        this.dateBirth = userDomain.getDateBirth();
        this.sex = userDomain.getSex();
        this.maritalStatus = userDomain.getMaritalStatus();
        this.city = userDomain.getCity();
        this.state = userDomain.getState();
        this.cpf = userDomain.getCpf();
        this.occupation = userDomain.getOccupation();

    }

    @Override
    public User toDomain() {

        return User
                .builder()
                .id(id)
                .name(name)
                .surname(surname)
                .phone(phone)
                .dateBirth(dateBirth)
                .sex(sex)
                .maritalStatus(maritalStatus)
                .city(city)
                .state(state)
                .cpf(cpf)
                .occupation(occupation)
                .build();

    }
}
