package br.voy.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import br.voy.infrastructure.model.UserModel;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByCpf(String cpf);

}
