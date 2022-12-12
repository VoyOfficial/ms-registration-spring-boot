package src.domain.repository;

import src.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    User saveUser(User userDomain);

    Optional<User> findByCpf(String cpf);

}
