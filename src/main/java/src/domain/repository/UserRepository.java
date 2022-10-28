package src.domain.repository;

import src.domain.entity.User;

public interface UserRepository {

    User saveUser(User userDomain);

}
