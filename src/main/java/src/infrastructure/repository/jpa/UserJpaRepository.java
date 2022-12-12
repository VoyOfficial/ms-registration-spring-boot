package src.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import src.infrastructure.model.UserModel;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByCpf(String cpf);

}
