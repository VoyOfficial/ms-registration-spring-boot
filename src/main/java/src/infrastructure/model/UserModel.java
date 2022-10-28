package src.infrastructure.model;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import src.domain.entity.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "user")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "marital_status_enum", typeClass = PostgreSQLEnumType.class)
public class UserModel extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private LocalDate dateBirth;

    private String genre;
    @Enumerated(EnumType.STRING)
    @Type(type = "marital_status_enum")
    @Column(name = "marital_status", nullable = false)
    private MaritalStatusEnum maritalStatus;
    private String city;
    private String state;
    private String cpf;
    private String occupation;

    public UserModel(User userDomain) {

        this.id = null;
        this.name = userDomain.getName();
        this.surname = userDomain.getSurname();
        this.phone = userDomain.getPhone();
        this.dateBirth = userDomain.getDateBirth();
        this.maritalStatus = MaritalStatusEnum.valueOf(userDomain.getMaritalStatus());
        this.genre = userDomain.getGenre();
        this.city = userDomain.getCity();
        this.state = userDomain.getState();
        this.cpf = userDomain.getCpf();
        this.occupation = userDomain.getOccupation();

    }

    @Override
    public User toDomain() {

        return User.builder().id(id).name(name).surname(surname).phone(phone).dateBirth(dateBirth).maritalStatus(maritalStatus.name()).genre(genre).city(city).state(state).cpf(cpf).occupation(occupation).build();

    }
}
