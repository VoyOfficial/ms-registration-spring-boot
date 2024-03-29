package src.domain.repository;

import src.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    User saveUser(User userDomain);

    Optional<User> findById(Long userId);

    Optional<User> findByCpf(String cpf);

}
