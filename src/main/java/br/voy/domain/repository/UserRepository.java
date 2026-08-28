package br.voy.domain.repository;

import br.voy.domain.entity.User;
import java.util.Optional;

public interface UserRepository {

    User saveUser(User userDomain);

    Optional<User> findById(Long userId);

    Optional<User> findByCpf(String cpf);
}
