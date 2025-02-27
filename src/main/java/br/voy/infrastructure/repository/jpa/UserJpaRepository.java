package br.voy.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import br.voy.infrastructure.model.UserModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByCpf(String cpf);

}
