package br.voy.infrastructure.repository.jpa;

import br.voy.infrastructure.model.UserModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByCpf(String cpf);
}
